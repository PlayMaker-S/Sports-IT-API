package PlayMakers.SportsIT.repository;

import PlayMakers.SportsIT.domain.JoinCompetition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JoinCompetitionRepository extends JpaRepository<JoinCompetition, JoinCompetition.JoinCompetitionId> {
    List<JoinCompetition> findByIdUid(Long uid);
    List<JoinCompetition> findByIdCompetitionId(Long competitionId);
    //Optional<JoinCompetition> findByUidAndCompetitionId(Long uid, Long competitionId);
    //Page<JoinCompetition> findByUid(Long uid, Pageable pageable);
    //Page<JoinCompetition> findByCompetitionId(Long competitionId, Pageable pageable);
    //void deleteByUidAndCompetitionId(Long uid, Long competitionId);
    //void deleteAllByCompetitionId(Long competitionId);

}
