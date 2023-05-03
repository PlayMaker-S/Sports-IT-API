package PlayMakers.SportsIT.repository;

import PlayMakers.SportsIT.domain.Competition;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

public interface CompetitionCustomRepository {
    Slice<Competition> findCompetitionSortedByCreatedDate(String keyword, Pageable pageable);
    Slice<Competition> findCompetitionSortedByViewCount(String keyword);
    Slice<Competition> findCompetitionSortedByScrapCount(String keyword);
    Slice<Competition> findCompetitionWithConditionsSortedByCreatedDate(String keyword, String condition);
    Slice<Competition> findCompetitionWithConditionsSortedByViewCount(String keyword, String condition);
    Slice<Competition> findCompetitionWithConditionsSortedByScrapCount(String keyword, String condition);
}
