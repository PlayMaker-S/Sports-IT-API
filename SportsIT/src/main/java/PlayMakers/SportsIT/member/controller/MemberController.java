package PlayMakers.SportsIT.member.controller;

import PlayMakers.SportsIT.member.domain.Member;
import PlayMakers.SportsIT.member.service.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
public class MemberController {
    //private final MemberService memberService = new MemberService();
    // 이런 방식으로 객체를 new로 생성하면 빈에 등록된 Service 객체를 받아와 사용하지 못함
    // 하나의 Service 객체를 여러 컨트롤러에서 공유해야 하기 때문에
    // DI를 통해 객체를 받아와야 함

    private final MemberService memberService;

    @Autowired // 스프링 컨테이너에 있는 MemberService 객체를 가져와서 연결
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }


    @PostMapping("/members/new")
    public String create(@RequestBody Member data){
        Member member = new Member();
        member.setId(data.getId());
        member.setPw(data.getPw());
        member.setName(data.getName());

        log.info("member = {}", member);
        //System.out.println("member = " + member.toString());
        memberService.join(member);

        return "ok";
    }

    @GetMapping("/members")
    public Member getProfile(@RequestParam Map<String, Object> paramMap) {
        Long id = Long.parseLong((String) paramMap.get("id"));
        Member member = memberService.findOne(id).get();

        return member;
    }
}
