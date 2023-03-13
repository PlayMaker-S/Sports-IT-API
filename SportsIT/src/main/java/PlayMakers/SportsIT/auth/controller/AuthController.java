package PlayMakers.SportsIT.auth.controller;

import PlayMakers.SportsIT.member.domain.MemberDto;
import PlayMakers.SportsIT.member.domain.MemberType;
import PlayMakers.SportsIT.member.service.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class AuthController {
    private final MemberService memberService;

    @Autowired
    public AuthController(MemberService memberService) {
        this.memberService = memberService;
    }

    //@Autowired
    //private BCryptPasswordEncoder bCryptPasswordEncoder;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestBody MemberDto data){
        log.info("member = {}", data);
        return "ok";
    }

    @GetMapping("/join")
    public String join(){
        return "join";
    }

    @PostMapping("/join")
    public String join(@RequestBody MemberDto data){
        log.info("member = {}", data);
        String rawPassword = data.getPw();
        //String encPassword = bCryptPasswordEncoder.encode(rawPassword);
        //data.setPw(encPassword);
        memberService.join(data);
        return "ok";
    }

    @Secured("ROLE_ADMIN")  // 특정 url만 권한 설정할 때
    @GetMapping("/admin/test")
    public String test() {
        return "관리자만 볼 수 있음";
    }

    @GetMapping("/login/oauth2/client/google")
    public String callback() {
        return "callback";
    }


    @GetMapping("/login?error")
    public String error() {
        return "login error";
    }

}
