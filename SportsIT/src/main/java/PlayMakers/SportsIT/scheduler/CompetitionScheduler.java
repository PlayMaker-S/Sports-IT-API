package PlayMakers.SportsIT.scheduler;

import PlayMakers.SportsIT.competitions.domain.Competition;
import PlayMakers.SportsIT.domain.CompetitionState;
import PlayMakers.SportsIT.repository.CompetitionRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
public class CompetitionScheduler {
    private final CompetitionRepository competitionRepository;

    public CompetitionScheduler(CompetitionRepository competitionRepository) {
        this.competitionRepository = competitionRepository;
    }

    @Async
    @Scheduled(fixedDelay = 1000 * 60 * 60) // 60분마다
    public void updateCompetitionState() {
        log.info("실시간 대회 상태 조회() start");
        LocalDateTime now = LocalDateTime.now();
        List<CompetitionState> targetStates = List.of(CompetitionState.PLANNING, CompetitionState.RECRUITING, CompetitionState.RECRUITING_END, CompetitionState.IN_PROGRESS);
        List<Competition> competitions = competitionRepository.findAllByStateIn(targetStates);

        for (Competition competition : competitions) {
            updateStateByTime(competition, now);
        }
    }

    @Transactional
    public Competition updateStateByTime(Competition competition, LocalDateTime now) {

        boolean isChanged = false;
        if (competition.getState() == CompetitionState.PLANNING && competition.getRecruitingEnd().isBefore(now)) {
            competition.setState(CompetitionState.RECRUITING);
            isChanged = true;
        }
        if (competition.getState() == CompetitionState.RECRUITING && competition.getRecruitingEnd().isBefore(now)) {
            competition.setState(CompetitionState.RECRUITING_END);
            isChanged = true;
        }
        if (competition.getState() == CompetitionState.RECRUITING_END && competition.getStartDate().isBefore(now)) {
            competition.setState(CompetitionState.IN_PROGRESS);
            isChanged = true;
        }
        if (competition.getState() == CompetitionState.IN_PROGRESS && competition.getEndDate().isBefore(now)) {
            competition.setState(CompetitionState.END);
            isChanged = true;
        }
        if (isChanged) {
            log.info("대회 상태 변경: {}", competition.getName());
            return competitionRepository.save(competition);
        }
        return null;
    }
}
