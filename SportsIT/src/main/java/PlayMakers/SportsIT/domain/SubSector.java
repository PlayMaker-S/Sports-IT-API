package PlayMakers.SportsIT.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Table(name = "subsector")
@Builder
@Getter
@AllArgsConstructor @NoArgsConstructor
public class SubSector {
    @Embeddable
    @Getter
    @AllArgsConstructor @NoArgsConstructor
    public static class SubSectorId implements Serializable {
        private Long competitionId;
        private Long sectorIndex;
        private Long subSectorIndex;
    }

    @EmbeddedId
    private SubSectorId subSectorId;

    private String name;

    @MapsId("sector_id")
    @ManyToOne(fetch = FetchType.LAZY, targetEntity = Sector.class)
    @JoinColumn(name = "sector_id")
    private Sector sector;
}
