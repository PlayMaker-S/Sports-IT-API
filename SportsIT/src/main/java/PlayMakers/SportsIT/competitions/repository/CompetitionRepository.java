package PlayMakers.SportsIT.competitions.repository;

import PlayMakers.SportsIT.competitions.domain.Competition;
import PlayMakers.SportsIT.competitions.enums.CompetitionState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface CompetitionRepository extends JpaRepository<Competition, Long>, CompetitionCustomRepository {
    // 대회 아이디로 찾기
    Optional<Competition> findByCompetitionId(Long competitionId);
    // 대회 이름으로 찾기
    Optional<Competition> findByName(String name);
    // 대회 상태로 찾기
    List<Competition> findByState(CompetitionState state);

    List<Competition> findAllByStateIn(List<CompetitionState> state);

    // 대회 저장


}
