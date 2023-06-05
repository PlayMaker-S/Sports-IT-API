package PlayMakers.SportsIT.controller;

import PlayMakers.SportsIT.domain.CompetitionResult;
import PlayMakers.SportsIT.dto.CompetitionResultDto;
import PlayMakers.SportsIT.service.CompetitionResultService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/competitionResult")
public class CompetitionResultController{

    private final CompetitionResultService competitionResultService;

    @Autowired // 스프링 컨테이너에 있는 CompetitionResultService 객체를 가져와서 연결
    public CompetitionResultController(CompetitionResultService competitionResultService) {
        this.competitionResultService = competitionResultService;
    }

    @PostMapping
    public ResponseEntity<CompetitionResult> createCompetitionResult(@RequestBody CompetitionResultDto competitionResultDto) {
        CompetitionResult competitionResult = competitionResultService.createCompetitionResult(competitionResultDto);
        return ResponseEntity.ok(competitionResult);
    }
}
