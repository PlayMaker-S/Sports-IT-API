package PlayMakers.SportsIT.controller;

import PlayMakers.SportsIT.domain.Member;
import PlayMakers.SportsIT.dto.LoginDto;
import PlayMakers.SportsIT.auth.security.jwt.JwtAuthenticationFilter;
import PlayMakers.SportsIT.auth.security.jwt.JwtTokenProvider;
import PlayMakers.SportsIT.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "1. 인증 API", description = "\uD83D\uDCAA 회원 가입/탈퇴, 로그인, 로그아웃 등 회원 권한 관련 API 목록입니다.")
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

    @Operation(summary = "로그인 API", description = """
            \uD83D\uDCCC 클라이언트에서 이메일과 비밀번호를 입력받아 인증을 진행합니다.\n\n
            ✔️ 성공시 생성된 JWT 토큰 값과 success: true를 반환합니다.\n\n
            ❌ 실패시 success: false를 반환합니다. (핸들러 추가 예정)
            """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공 (Authorization 헤더에서 토큰 가져올 것)"),
            @ApiResponse(responseCode = "401", description = "로그인 실패")
    })
    @PostMapping("/authenticate")
    public ResponseEntity<Object> authorize(@RequestBody LoginDto loginDto) {

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginDto.getLoginId(), loginDto.getPw());

        // authenticate 메서드 실행 시 PrincipalDetailsService의 loadUserByUsername 메서드 실행
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
