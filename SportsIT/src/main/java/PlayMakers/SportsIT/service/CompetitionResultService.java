package PlayMakers.SportsIT.service;

import PlayMakers.SportsIT.domain.CompetitionResult;
import PlayMakers.SportsIT.dto.CompetitionResultDto;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Transactional
public class CompetitionResultService {

    public CompetitionResult createCompetitionResult(CompetitionResultDto competitionResultDto) {

    }
}
