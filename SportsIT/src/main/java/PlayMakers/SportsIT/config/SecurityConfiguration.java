package PlayMakers.SportsIT.config;

import PlayMakers.SportsIT.auth.CustomOAuth2UserService;
import PlayMakers.SportsIT.auth.PrincipalDetailsService;
import PlayMakers.SportsIT.auth.security.cookie.CookieAuthorizationRequestRepository;
import PlayMakers.SportsIT.auth.security.handler.OAuth2AuthenticationFailureHandler;
import PlayMakers.SportsIT.auth.security.handler.OAuth2AuthenticationSuccessHandler;
import PlayMakers.SportsIT.auth.security.jwt.*;
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
    private final PrincipalDetailsService principalDetailsService;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final CookieAuthorizationRequestRepository cookieAuthorizationRequestRepository;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;
    private final JwtTokenProvider tokenProvider;
    //private final CorsFilter corsFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();  // 비밀번호 암호화 제공
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers("/favicion.io/**", "/.well-known/**");
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                // 기본 HTTP 설정
                .cors()
                .and()
                .httpBasic().disable() // rest api 이므로 기본설정 사용 안함. 기본설정은 비인증시 로그인폼 화면으로 redirect
                .csrf().disable()  // csrf 비활성화 -> token 방식이므로
                .formLogin().disable()  // form 로그인 비활성화
                .rememberMe().disable()  // rememberMe 비활성화
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // jwt token으로 인증하므로 세션은 필요 없으므로 생성 안함


                // 인가 설정
                .and()
                .authorizeHttpRequests() // HttpServletRequest를 사용하는 요청들에 대한 접근제한 설정
                .requestMatchers("/api/login").permitAll()  // 로그인은 누구나 가능
                .requestMatchers("/api/*").permitAll()
                .requestMatchers("/oauth2/**").permitAll()
                .requestMatchers("/members/**").authenticated()  // 인증만 필요
                .requestMatchers("/institution/**").hasAnyRole("ROLE_INSTITUTION", "ROLE_ADMIN")
                .requestMatchers("/admin/**").hasRole("ROLE_ADMIN")  // 권한도 필요
                .anyRequest().permitAll()
                .and()
        // filter 설정

                .addFilterBefore(new JwtAuthenticationFilter(tokenProvider), UsernamePasswordAuthenticationFilter.class)  // jwt 필터 추가
                .userDetailsService(principalDetailsService)
                .exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(jwtAccessDeniedHandler)

                // enable h2-console
                .and().headers().frameOptions().sameOrigin().and()

        // OAuth2 설정

                .oauth2Login()
                .authorizationEndpoint().baseUri("/oauth2/authorization") // OAuth2 로그인 요청을 처리할 Endpoint
                .authorizationRequestRepository(cookieAuthorizationRequestRepository)
                .and()
                .redirectionEndpoint().baseUri("/oauth2/callback/*") // OAuth2 로그인 이후 사용자 정보를 가져올 Endpoint
                .and()
                .userInfoEndpoint().userService(customOAuth2UserService) // OAuth2 로그인 성공 이후 사용자 정보를 가져올 때의 설정들을 담당
                .and()
                .successHandler(oAuth2AuthenticationSuccessHandler) // 로그인 성공 시 후속 조치를 진행할 OAuth2UserService 인터페이스의 구현체 등록
                .failureHandler(oAuth2AuthenticationFailureHandler) // 로그인 실패 시 후속 조치를 진행할 OAuth2UserService 인터페이스의 구현체 등록
                .and()
        // 로그아웃 설정

                .logout()
                .clearAuthentication(true) // 로그아웃 시 인증정보 삭제
                .deleteCookies("JSESSIONID") // 로그아웃 시 쿠키 삭제

        // JWT 설정
                .and()
                .requiresChannel(channel ->
                        channel.anyRequest().requiresSecure())
                .authorizeHttpRequests(authorize ->
                        authorize.anyRequest().permitAll())
                .apply(new JwtSecurityConfig(tokenProvider));


        return http.build();
    }
}
