package PlayMakers.SportsIT.config;

import PlayMakers.SportsIT.auth.security.CorsFilter;
import PlayMakers.SportsIT.auth.security.jwt.JwtAccessDeniedHandler;
import PlayMakers.SportsIT.auth.security.jwt.JwtAuthenticationEntryPoint;
import PlayMakers.SportsIT.auth.security.jwt.JwtSecurityConfig;
import PlayMakers.SportsIT.auth.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Slf4j
@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    private final JwtTokenProvider tokenProvider;
    private final CorsFilter corsFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();  // 비밀번호 암호화 제공
    }

//    @Bean
//    public Jwt jwt() {
//        return new Jwt(
//                this.jwtConfigure.issuer(),
//                this.jwtConfigure.clientSecret(),
//                this.jwtConfigure.accessToken(),
//                this.jwtConfigure.refreshToken()
//        );
//    }
//
//    public JWTAuthenticationFilter jwtAuthenticationFilter(Jwt jwt, TokenService tokenService) {
//        return new JWTAuthenticationFilter(
//                this.jwtConfigure.accessToken().header(),
//                this.jwtConfigure.refreshToken().header(),
//                jwt,
//                tokenService
//        );
//    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers("/favicion.io/**");
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf().disable()  // csrf 비활성화 -> token 방식이므로
                .addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class)  // cors 필터 추가
                .exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(jwtAccessDeniedHandler)

                // enable h2-console
                .and().headers().frameOptions().sameOrigin()

                // 세션 사용 안함
                .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and()
                .authorizeHttpRequests()
                .requestMatchers("/api/signup").permitAll()  // 회원가입은 누구나 가능
                .requestMatchers("/api/login").permitAll()  // 로그인은 누구나 가능
                .requestMatchers("/api/authenticate").permitAll()
                .requestMatchers("/api/member/all").permitAll()
                .requestMatchers("/members/**").authenticated()  // 인증만 필요
                .requestMatchers("/institution/**").hasAnyRole("ROLE_INSTITUTION", "ROLE_ADMIN")
                .requestMatchers("/admin/**").hasRole("ROLE_ADMIN")  // 권한도 필요
                .requestMatchers("/**").permitAll()
                .and().formLogin().loginPage("/loginform.html").defaultSuccessUrl("/")// 로그인 페이지
                .and().logout().logoutSuccessUrl("/login")  // 로그아웃시 로그인 페이지로 이동
                .and().oauth2Login().loginPage("/loginform.html").defaultSuccessUrl("/")  // OAuth2 로그인
                .and()
                .requiresChannel(channel ->
                        channel.anyRequest().requiresSecure())
                .authorizeHttpRequests(authorize ->
                        authorize.anyRequest().permitAll())
                .apply(new JwtSecurityConfig(tokenProvider));

        return http.build();
    }
}
