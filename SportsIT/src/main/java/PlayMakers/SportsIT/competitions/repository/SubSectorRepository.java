package PlayMakers.SportsIT.repository;

import PlayMakers.SportsIT.competitions.domain.Sector;
import PlayMakers.SportsIT.competitions.domain.SubSector;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubSectorRepository extends JpaRepository<SubSector, SubSector.SubSectorId> {
    List<SubSector> findAllBySubSectorId_CompetitionIdAndSubSectorId_SectorIndex(Long competitionId, Long sectorIndex);
    List<SubSector> findAllBySector(Sector sector);
    Optional<SubSector> findTopBySubSectorId_CompetitionIdAndSubSectorId_SectorIndexOrderBySubSectorId_SubSectorIndexDesc(Long competitionId, Long sectorIndex);
    Optional<SubSector> findTopBySectorOrderBySubSectorId_SubSectorIndexDesc(Sector sector);
}
