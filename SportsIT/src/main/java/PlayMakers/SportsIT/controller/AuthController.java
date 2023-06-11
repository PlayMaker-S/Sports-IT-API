package PlayMakers.SportsIT.controller;

import PlayMakers.SportsIT.domain.Member;
import PlayMakers.SportsIT.dto.LoginDto;
import PlayMakers.SportsIT.dto.TokenDto;
import PlayMakers.SportsIT.auth.security.jwt.JwtAuthenticationFilter;
import PlayMakers.SportsIT.auth.security.jwt.JwtTokenProvider;
import PlayMakers.SportsIT.repository.MemberRepository;
import PlayMakers.SportsIT.service.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@Slf4j
@RestController
@RequestMapping("/api")
public class AuthController {
    private final JwtTokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final MemberService memberService;


    public AuthController(JwtTokenProvider tokenProvider, AuthenticationManagerBuilder authenticationManagerBuilder,
                          MemberService memberService) {
        try {
            this.tokenProvider = tokenProvider;
            this.authenticationManagerBuilder = authenticationManagerBuilder;
            this.memberService = memberService;
        } catch (Exception e) {
            throw new RuntimeException("생성 실패");
        }
    }

    @PostMapping("/authenticate")
    public ResponseEntity<Object> authorize(@RequestBody LoginDto loginDto) {

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginDto.getLoginId(), loginDto.getPw());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.createToken(authentication); // JWT Token 생성

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JwtAuthenticationFilter.AUTHORIZATION_HEADER, "Bearer " + jwt);
        Member member = memberService.findOne(loginDto.getLoginId());
        HashMap<String, Object> res = new HashMap<>(){{
            put("token", jwt);
            put("role", member.getMemberType());
            put("email", member.getEmail());
            put("name", member.getName());
            put("uid", member.getUid());
        }};

        return new ResponseEntity<>(res, httpHeaders, HttpStatus.OK);
    }
}
