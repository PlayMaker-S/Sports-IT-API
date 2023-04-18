package PlayMakers.SportsIT.controller;

import PlayMakers.SportsIT.domain.Member;
import PlayMakers.SportsIT.dto.MemberDto;
import PlayMakers.SportsIT.service.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api")
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


    @PostMapping("/signup")
    public ResponseEntity<Member> signup(@RequestBody MemberDto data){
        log.info("member = {}", data);
        //System.out.println("member = " + member.toString());

        return ResponseEntity.ok(memberService.join(data));
    }

<<<<<<< HEAD:SportsIT/src/main/java/PlayMakers/SportsIT/controller/MemberController.java
    @GetMapping("/members")
    public Member getProfile(@RequestParam Map<String, Object> paramMap) {
        log.debug("paramMap = {}", paramMap.get("id"));
        Long id = Long.parseLong((String) paramMap.get("id"));
        Member member = memberService.findOne(id).get();
        log.debug("member = {}", member);
        log.info("member = {}", member);
        log.info("member = {}", member);

=======
    @GetMapping("/member")
    public List<Member> getProfile(@RequestParam Map<String, Object> paramMap, @RequestBody Map<String, Object> data) {
        log.info("paramMap = {}", data.get("name"));
        String name = String.valueOf(data.get("name"));
        return memberService.findByName(name);
    }
>>>>>>> 2d21fe216a200afba4b9989d787d429e7d575dc6:SportsIT/src/main/java/PlayMakers/SportsIT/member/controller/MemberController.java

    @GetMapping("/member/all")
    public List<Member> getMemberAll(){
        return memberService.findMembers();
    }

    // Test
    @GetMapping("/member/home")
    public String home() {
        return "home";
    }
}
