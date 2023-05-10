package PlayMakers.SportsIT.repository;

import PlayMakers.SportsIT.domain.JoinCompetition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JoinCompetitionRepository extends JpaRepository<JoinCompetition, JoinCompetition.JoinCompetitionId> {
    List<JoinCompetition> findByIdUid(Long uid);
    List<JoinCompetition> findByIdCompetitionId(Long competitionId);
    Optional<JoinCompetition> findByIdUidAndIdCompetitionId(Long uid, Long competitionId);
    void deleteByIdUidAndIdCompetitionId(Long uid, Long competitionId);
    void deleteByIdCompetitionId(Long competitionId);
    void deleteByIdUid(Long uid);
    int countByIdCompetitionId(Long competitionId);
    int countByIdCompetitionIdAndJoinType(Long competitionId, JoinCompetition.joinType type);
}
