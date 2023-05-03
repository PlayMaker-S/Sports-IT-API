package PlayMakers.SportsIT.repository;

import PlayMakers.SportsIT.domain.Competition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface CompetitionRepository extends JpaRepository<Competition, Long>, CompetitionCustomRepository {
    // 대회 아이디로 찾기
    Competition findByCompetitionId(Long competitionId);
    // 대회 이름으로 찾기
    Optional<Competition> findByName(String name);
    // 대회 상태로 찾기
    Optional<Competition> findByState(String state);

    // 대회 저장


}
