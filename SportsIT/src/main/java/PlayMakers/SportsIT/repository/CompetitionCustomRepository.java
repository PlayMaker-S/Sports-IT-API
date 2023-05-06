package PlayMakers.SportsIT.repository;

import PlayMakers.SportsIT.domain.Competition;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;
import java.util.Map;

public interface CompetitionCustomRepository {
    Slice<Competition> findCompetitionBySlice(String keyword, List<String> filterType, Pageable pageable);

}
