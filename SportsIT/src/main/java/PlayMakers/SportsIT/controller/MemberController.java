package PlayMakers.SportsIT.controller;

import PlayMakers.SportsIT.domain.Feed;
import PlayMakers.SportsIT.domain.HostProfile;
import PlayMakers.SportsIT.domain.Member;
import PlayMakers.SportsIT.dto.MemberDto;
import PlayMakers.SportsIT.service.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.Host;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.*;

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

    @GetMapping("/member")
    public List<Member> getProfile(@RequestParam Map<String, Object> paramMap, @RequestBody Map<String, Object> data) {
        log.info("paramMap = {}", data.get("name"));
        String name = String.valueOf(data.get("name"));
        return memberService.findByName(name);
    }

    @GetMapping("/member/all")
    public List<Member> getMemberAll(){
        return memberService.findMembers();
    }

    // Test
    @GetMapping("/member/home")
    public String home() {
        return "home";
    }

    @GetMapping("/member/checkEmail")
    public String checkEmail(@RequestBody Map<String, Object> data) {
        String email = String.valueOf(data.get("email"));
        Boolean isDuplicate = memberService.isDuplicateEmail(email);
        if (isDuplicate) {
            return "중복된 이메일로 가입된 계정이 있습니다.";
        } else {
            return "중복된 계정이 없습니다.";
        }
    }
    @GetMapping("member/feeds")
    public ResponseEntity<Map> getAllFeedsByMember(@AuthenticationPrincipal User user) {
        String memberEmail = user.getUsername();
        Member member = memberService.findOne("yuk@naver.com");
        List<Feed> feeds = memberService.getAllFeedsByMember(member);
        for(Feed feed : feeds) {
            log.info("id : {}", feed.getFeedId());
            log.info("url : {}", feed.getImgUrl());
            log.info("member : {}", feed.getMember());
        }
        Map res = new HashMap<String, List<Feed>>() {{
            put("feeds", feeds);
        }};
        return ResponseEntity.ok(res);
    }

    @GetMapping("member/hostProfile")
    public ResponseEntity<HostProfile> getHostProfile(@AuthenticationPrincipal User user) {
        log.info("Hi!");
        String memberEmail = user.getUsername();
        Member member = memberService.findOne(memberEmail);
        HostProfile hostProfile = memberService.getHostProfileByMember(member);
        return ResponseEntity.ok(hostProfile);
    }
}
