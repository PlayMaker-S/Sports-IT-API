package PlayMakers.SportsIT.competitions.service;

import PlayMakers.SportsIT.competitions.domain.Category;
import PlayMakers.SportsIT.competitions.domain.Competition;
import PlayMakers.SportsIT.domain.Member;
import org.springframework.data.domain.Slice;

import java.util.List;
import java.util.Set;

public interface CompetitionService {
    // 생성
    Competition createCompetition(Member host, Competition competition, Set<Category> categories);

    // 수정
    Competition updateCompetition(Long competitionId, Competition updated);

    // 조회
    Competition findCompetition(Long competitionId);
    Slice<Competition> findCompetitionsWithConditions(String keyword, List<String> filteringConditions, String orderBy, int page, int size);
    Slice<Competition> findCompetitionsWithHostId(Long hostId, int page, int size);

    // 삭제
    void deleteCompetition(Long competitionId);
}
