package PlayMakers.SportsIT.auth.controller;

import PlayMakers.SportsIT.auth.dto.LoginDto;
import PlayMakers.SportsIT.auth.dto.TokenDto;
import PlayMakers.SportsIT.auth.security.jwt.JwtAuthenticationFilter;
import PlayMakers.SportsIT.auth.security.jwt.JwtTokenProvider;
import PlayMakers.SportsIT.member.domain.MemberDto;
import PlayMakers.SportsIT.member.domain.MemberType;
import PlayMakers.SportsIT.member.service.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api")
public class AuthController {
    private final JwtTokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;


    public AuthController(JwtTokenProvider tokenProvider, AuthenticationManagerBuilder authenticationManagerBuilder) {
        try {
            this.tokenProvider = tokenProvider;
            this.authenticationManagerBuilder = authenticationManagerBuilder;
        } catch (Exception e) {
            throw new RuntimeException("생성 실패");
        }
    }

    @PostMapping("/authenticate")
    public ResponseEntity<TokenDto> authorize(@RequestBody LoginDto loginDto) {

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginDto.getLoginId(), loginDto.getPw());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.createToken(authentication); // JWT Token 생성

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JwtAuthenticationFilter.AUTHORIZATION_HEADER, "Bearer " + jwt);

        return new ResponseEntity<>(new TokenDto(jwt), httpHeaders, HttpStatus.OK);
    }
}
