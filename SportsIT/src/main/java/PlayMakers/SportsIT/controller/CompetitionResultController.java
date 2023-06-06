package PlayMakers.SportsIT.controller;

import PlayMakers.SportsIT.domain.CompetitionResult;
import PlayMakers.SportsIT.dto.CompetitionResultDto;
import PlayMakers.SportsIT.service.CompetitionResultService;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    public ResponseEntity<String> createCompetitionResult(@RequestBody Map<String, List<CompetitionResultDto>> param) {
        List<CompetitionResultDto> dtoList = param.get("info");
        return ResponseEntity.ok(competitionResultService.createCompetitionResult(dtoList));
    }
}
