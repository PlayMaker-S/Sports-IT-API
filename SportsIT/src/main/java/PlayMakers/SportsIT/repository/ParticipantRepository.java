package PlayMakers.SportsIT.repository;

import PlayMakers.SportsIT.domain.Participant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ParticipantRepository extends JpaRepository<Participant, Participant.ParticipantId> {
    List<Participant> findAllByCompetitionCompetitionId(Long competitionId);
    List<Participant> findAllByCompetitionCompetitionIdAndMemberUid(Long competitionId, Long uid);
    List<Participant> findAllByCompetitionCompetitionIdAndIdSectorTitle(Long competitionId, String sectorTitle);
    List<Participant> findAllByCompetitionCompetitionIdAndIdSectorTitleAndIdSubSectorName(Long competitionId, String sectorTitle, String subSectorName);

}
