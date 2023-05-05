package PlayMakers.SportsIT.repository;

import PlayMakers.SportsIT.domain.Competition;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface CompetitionCustomRepository {
    Slice<Competition> findCompetitionBySlice(String keyword, String filterType, Pageable pageable);

}
