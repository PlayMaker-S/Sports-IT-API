package PlayMakers.SportsIT.repository;

import PlayMakers.SportsIT.domain.Competition;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface CompetitionCustomRepository {
    Slice<Competition> findCompetitionBySlice(String keyword, List<String> filterType, Pageable pageable);
    // member가 참가한 대회 목록 조회

    Slice<Competition> findCompetitionsBySliceWithIdsAndUid(List<Long> competitionIds, Long uid, Pageable pageable);
}
