package PlayMakers.SportsIT.controller;

import PlayMakers.SportsIT.domain.BodyInfo;
import PlayMakers.SportsIT.domain.Member;
import PlayMakers.SportsIT.dto.BodyInfoDto;
import PlayMakers.SportsIT.service.BodyInfoService;
import PlayMakers.SportsIT.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bodyInfo")
public class BodyInfoController {
    private final BodyInfoService bodyInfoService;
    private final MemberService memberService;

    @PostMapping
    public ResponseEntity<BodyInfo> createBodyInfo(@RequestBody BodyInfoDto dto,
                                                   @AuthenticationPrincipal User user){
        String memberEmail = user.getUsername(); // 로그인한 회원 ID를 가져옴
        Member member = memberService.findOne(memberEmail);
        dto.setMember(member);

        BodyInfo bodyInfo = bodyInfoService.create(dto);
        return ResponseEntity.ok(bodyInfo);
    }

    @GetMapping
    public ResponseEntity<Optional<BodyInfo>> getBodyInfo(@AuthenticationPrincipal User user) {
        String memberEmail = user.getUsername();
        Member member = memberService.findOne(memberEmail);
        Optional<BodyInfo> bodyInfo = bodyInfoService.getBodyInfo(member);
        return ResponseEntity.ok(bodyInfo); // 200
    }

    @PutMapping("/{bodyInfoId}")
    public ResponseEntity<BodyInfo> updateBodyInfo(@PathVariable("bodyInfoId") Long bodyInfoId, @RequestBody BodyInfoDto dto){

        BodyInfo updatedBodyInfo = bodyInfoService.update(bodyInfoId, dto);
        return ResponseEntity.ok(updatedBodyInfo);
    }
}
