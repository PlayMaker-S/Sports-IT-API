package PlayMakers.SportsIT.controller;

import PlayMakers.SportsIT.domain.Member;
import PlayMakers.SportsIT.dto.CompetitionDto;
import PlayMakers.SportsIT.service.CompetitionService;
import PlayMakers.SportsIT.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import PlayMakers.SportsIT.domain.Competition;

import java.net.URI;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/competitions")
public class CompetitionController {
    private final CompetitionService competitionService;
    private final MemberService memberService;

    @PostMapping
    public ResponseEntity<Competition> createCompetition(@RequestBody CompetitionDto dto,
                                                         @AuthenticationPrincipal User user) throws Exception{
        // 주최자 ID 설정 - 일단 dto에 memberId가 포함된다고 가정
        String hostEmail = user.getUsername(); // 로그인한 회원 ID를 가져옴
        Member host = memberService.findOne(hostEmail);
        dto.setHost(host);

        // 대회 생성
        Competition competition = competitionService.create(dto);

        return ResponseEntity.created(URI.create("/" + competition.getCompetitionId())) // Location Header에 생성된 리소스의 URI를 담아서 보냄
                .body(competition); // 201
    }

    @GetMapping
    public ResponseEntity<Optional<Competition>> getCompetitions() {
        //List<Competition> competitions = competitionService.getCompetitions();
        Optional<Competition> competitions = null;
        return ResponseEntity.ok(competitions); // 200
    }

    @GetMapping("/{competitionId}")
    public ResponseEntity<Competition> getCompetition(@PathVariable Long competitionId) {
        //Competition competition = competitionService.getCompetition(competitionId);
        Competition competition = null;
        return ResponseEntity.ok(competition); // 200
    }

    @PutMapping("/{competitionId}")
    public ResponseEntity<Competition> updateCompetition(@PathVariable Long competitionId, @RequestBody CompetitionDto dto) {
        Competition competition = competitionService.update(competitionId, dto);
        return ResponseEntity.ok(competition); // 200
    }

    @DeleteMapping("/{competitionId}")
    public ResponseEntity<Void> deleteCompetition(@PathVariable Long competitionId) {
        competitionService.delete(competitionId);
        return ResponseEntity.noContent().build(); // 204
    }
}
