package PlayMakers.SportsIT.domain;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@AllArgsConstructor @NoArgsConstructor
@Getter
@Builder
public class Participant extends BaseEntity{
    @EmbeddedId
    private ParticipantId id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "uid")
    @MapsId("uid")
    private Member member;

    @ManyToOne(optional = false)
    @JoinColumn(name = "competition_id")
    @MapsId("competitionId")
    private Competition competition;

    @Embeddable
    @AllArgsConstructor @NoArgsConstructor
    @Builder
    @Getter @Setter
    public static class ParticipantId implements Serializable{
        private Long uid;
        private Long competitionId;
        @Column(nullable = false, length = 50)
        private String sectorTitle;
        @Column(nullable = false, length = 50)
        private String subSectorName;
    }
}
