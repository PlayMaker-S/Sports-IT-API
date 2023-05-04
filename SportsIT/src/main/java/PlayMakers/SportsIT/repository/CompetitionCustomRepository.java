package PlayMakers.SportsIT.repository;

import PlayMakers.SportsIT.domain.Competition;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface CompetitionCustomRepository {
    Slice<Competition> findCompetitionBySlice(String keyword, String filterType, Pageable pageable);
    /*
        @filterType String "recruitingEnd", "totalPrize", "recommend"
    */
    Slice<Competition> findCompetitionSortedByViewCount(String keyword);
    Slice<Competition> findCompetitionSortedByScrapCount(String keyword);
    Slice<Competition> findCompetitionWithConditionsSortedByCreatedDate(String keyword, String condition);
    Slice<Competition> findCompetitionWithConditionsSortedByViewCount(String keyword, String condition);
    Slice<Competition> findCompetitionWithConditionsSortedByScrapCount(String keyword, String condition);
}
