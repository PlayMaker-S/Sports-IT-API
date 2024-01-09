package PlayMakers.SportsIT.repository;

import PlayMakers.SportsIT.competitions.domain.Competition;
import PlayMakers.SportsIT.competitions.domain.Sector;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SectorRepository extends JpaRepository<Sector, Sector.SectorId> {
    List<Sector> findAllByCompetition_CompetitionId(Long competitionId);
    List<Sector> findAllByCompetition(Competition competition);
    Optional<Sector> searchTopByCompetition_CompetitionIdOrderBySectorId_SectorIndexDesc(Long competitionId);
}
