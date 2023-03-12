package PlayMakers.SportsIT.config;

import PlayMakers.SportsIT.member.domain.MemberType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.authorizeHttpRequests()
                .requestMatchers("/members/**").authenticated()  // 인증만 필요
                .requestMatchers("/institution/**").hasAnyRole(MemberType.단체.toString(), MemberType.관리자.toString())
                .requestMatchers("/admin/**").hasRole(MemberType.관리자.toString())  // 권한도 필요
                .requestMatchers("/**").permitAll()
                .and().formLogin().loginPage("/login").defaultSuccessUrl("/")// 로그인 페이지
                .and().logout().logoutSuccessUrl("/login");  // 로그아웃시 로그인 페이지로 이동



        return http.build();
    }
}
