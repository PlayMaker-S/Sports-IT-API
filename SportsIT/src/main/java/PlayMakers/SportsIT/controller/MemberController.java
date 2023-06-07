package PlayMakers.SportsIT.controller;

import PlayMakers.SportsIT.domain.Feed;
import PlayMakers.SportsIT.domain.HostProfile;
import PlayMakers.SportsIT.domain.Member;
import PlayMakers.SportsIT.dto.MemberDto;
import PlayMakers.SportsIT.service.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("/member/checkEmail")
    public ResponseEntity<Object> checkEmail(@RequestBody Map<String, Object> data) {
        Map<String, Object> res = new HashMap<>();
        String email = String.valueOf(data.get("email"));
        Boolean isDuplicate = memberService.isDuplicatedEmail(email);
        if (isDuplicate) {
            res.put("isDuplicated", true);
            res.put("message", "중복된 이메일로 가입된 계정이 있습니다.");
        } else {
            res.put("isDuplicated", false);
            res.put("message", "사용 가능한 이메일입니다.");
        }
        return ResponseEntity.ok(res);
    }
    @PostMapping("/member/checkPhone")
    public ResponseEntity<Object> checkPhone(@RequestBody Map<String, Object> data) {
        Map<String, Object> res = new HashMap<>();
        String phoneNumber = String.valueOf(data.get("phone"));
        boolean isDuplicate = memberService.isDuplicatedPhone(phoneNumber);
        if(isDuplicate) {
            res.put("isDuplicate", true);
            res.put("message", "중복된 전화번호로 가입된 계정이 있습니다.");
        } else {
            res.put("isDuplicate", false);
            res.put("message", "사용 가능한 전화번호입니다.");
        }
        return ResponseEntity.ok(res);
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

    @GetMapping("member/find/id")
    public ResponseEntity<Object> findIdWithPhone(@RequestParam String phoneNumber) {
        Map<String, Object> res = new HashMap<>();
        String id = memberService.findEmailByPhone(phoneNumber);
        if(id == null) {
            res.put("isExist", false);
            res.put("message", "가입된 계정이 없습니다.");
        } else {
            res.put("isExist", true);
            res.put("message", "아이디는 " + id + "입니다.");
        }
        return ResponseEntity.ok(res);
    }
    @PostMapping("member/find/password")
    public ResponseEntity<Object> findPasswordWithEmailAndPhone(@RequestBody Map<String, Object> data) {
        String email = String.valueOf(data.get("email"));
        String phone = String.valueOf(data.get("phone"));
        log.info("비밀번호 찾기 요청: {} {}", email, phone);
        Map<String, Object> res = new HashMap<>();
        if (memberService.isExistsWithPhoneAndEmail(email, phone)) {
            String newPassword = memberService.generateNewPassword(email);
            res.put("success", true);
            res.put("message", "임시 비밀번호는 "+ newPassword +"입니다.");
        }
        else {
            res.put("success", false);
            res.put("message", "입력한 정보와 일치하는 계정이 없습니다.");
        }
        return ResponseEntity.ok(res);
    }
}
