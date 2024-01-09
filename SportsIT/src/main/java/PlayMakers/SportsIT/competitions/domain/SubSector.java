package PlayMakers.SportsIT.competitions.domain;

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
        @Column(name = "cmp_id")
        private Long competitionId;
        @Column(name = "sector_index")
        private Long sectorIndex;
        @Column(name = "subsector_index")
        private Long subSectorIndex;
    }

    @EmbeddedId
    private SubSectorId subSectorId;

    private String name;

    @MapsId("sector_id")
    @ManyToOne(fetch = FetchType.LAZY, targetEntity = Sector.class)
    @JoinColumns({
            @JoinColumn(name = "cmp_id", referencedColumnName = "competition_id"),
            @JoinColumn(name = "sector_index", referencedColumnName = "sector_index")
    })
    private Sector sector;
}
