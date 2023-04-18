package PlayMakers.SportsIT.service;

import PlayMakers.SportsIT.domain.Competition;
import PlayMakers.SportsIT.dto.CompetitionDto;
import PlayMakers.SportsIT.repository.CompetitionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class CompetitionService {
    private final CompetitionRepository competitionRepository;

    public Competition createCompetition(CompetitionDto dto) {
        Competition newCompetition = Competition.builder()
                .name(dto.getName())
                .host(dto.getHostEmail())
                .sportsType(dto.getSportsType())
                .content(dto.getContent())
                .location(dto.getLocation())
                .state(dto.getState())
                .stateDetail(dto.getStateDetail())
                .startDate(dto.getStartDate())
                .recruitingStart(dto.getRecruitingStart())
                .recruitingEnd(dto.getRecruitingEnd())
                .build();

        return competitionRepository.save(newCompetition);
    }

}
