package PlayMakers.SportsIT.scheduler;

import PlayMakers.SportsIT.repository.CompetitionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CompetitionScheduler {
    private final CompetitionRepository competitionRepository;

    public CompetitionScheduler(CompetitionRepository competitionRepository) {
        this.competitionRepository = competitionRepository;
    }

    @Async
    @Scheduled(fixedDelay = 1000 * 60) // 1분마다
    public void updateCompetitionState() {
        
    }
}
