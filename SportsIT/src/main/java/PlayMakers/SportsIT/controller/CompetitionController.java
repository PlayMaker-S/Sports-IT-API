package PlayMakers.SportsIT.controller;

import PlayMakers.SportsIT.service.CompetitionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/competition")
public class CompetitionController {
    private final CompetitionService competitionService;
}
