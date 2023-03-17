package PlayMakers.SportsIT.auth.security.jwt;

import PlayMakers.SportsIT.auth.security.jwt.JwtAuthenticationFilter;
import PlayMakers.SportsIT.auth.security.jwt.JwtTokenProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * TokenProvider를 통해 전달받은 토큰의 유효성을 검증하는 필터를 Security 로직에 등록한다.
 */
public class JwtSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {
    private JwtTokenProvider tokenProvider;

    public JwtSecurityConfig(JwtTokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    public void configure(HttpSecurity http) {
        JwtAuthenticationFilter customFilter = new JwtAuthenticationFilter(tokenProvider);
        http.addFilterBefore(customFilter, UsernamePasswordAuthenticationFilter.class);
    }

}

//https://velog.io/@seho100/Spring-boot%EB%A5%BC-%ED%99%9C%EC%9A%A9%ED%95%9C-JWT-%EA%B5%AC%ED%98%84
