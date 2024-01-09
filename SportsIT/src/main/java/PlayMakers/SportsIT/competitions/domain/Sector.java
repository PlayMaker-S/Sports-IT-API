package PlayMakers.SportsIT.competitions.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "sector")
@Builder
@Getter
@AllArgsConstructor @NoArgsConstructor
public class Sector {
    @Embeddable
    @Getter
    @AllArgsConstructor @NoArgsConstructor
    public static class SectorId implements Serializable {
        @Column(name = "competition_id")
        private Long competitionId;
        @Column(name = "sector_index")
        private Long sectorIndex;
    }

    @EmbeddedId
    private SectorId sectorId;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false)
    private Long cost;

    @Column(nullable = false)
    private Long expandCost;

    @Column(nullable = false)
    private boolean multi; // true 면 다중선택, false 면 단일선택

    @MapsId("competition_id")
    @ManyToOne(fetch = FetchType.LAZY, targetEntity = Competition.class)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "cmp_id")
    private Competition competition;

    @OneToMany(mappedBy = "sector", cascade = CascadeType.ALL)
    private List<SubSector> subSectors;

}
