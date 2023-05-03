package PlayMakers.SportsIT.controller;

import PlayMakers.SportsIT.domain.BodyInfo;
import PlayMakers.SportsIT.domain.Competition;
import PlayMakers.SportsIT.domain.Member;
import PlayMakers.SportsIT.dto.BodyInfoDto;
import PlayMakers.SportsIT.dto.CompetitionDto;
import PlayMakers.SportsIT.service.BodyInfoService;
import PlayMakers.SportsIT.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bodyInfo")
public class BodyInfoController {
    private final BodyInfoService bodyInfoService;
    private final MemberService memberService;

    @PostMapping
    public ResponseEntity<BodyInfo> createBodyInfo(@RequestBody BodyInfoDto dto){
        Member member = memberService.findOne("sportsit_test@abc.com");
        dto.setMember(member);

        BodyInfo bodyInfo = bodyInfoService.create(dto);
        return ResponseEntity.ok(bodyInfo);
    }
}
