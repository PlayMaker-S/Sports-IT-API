package PlayMakers.SportsIT.domain;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Getter
@AllArgsConstructor @NoArgsConstructor
@Builder
public class JoinCompetition extends BaseEntity{
    @EmbeddedId
    private JoinCompetitionId id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "uid")
    @MapsId("uid")
    private Member member;

    @ManyToOne(optional = false)
    @JoinColumn(name = "competition_id")
    @MapsId("competitionId")
    private Competition competition;

    @Column(nullable = false)
    private joinType joinType;

    @Column
    private String formId;

    @Column(nullable = false)
    private boolean isAgree;

    @Column(nullable = false)
    private boolean isPaid;

    @Embeddable
    @AllArgsConstructor @NoArgsConstructor
    @Setter
    public static class JoinCompetitionId implements Serializable {
        private Long uid;
        private Long competitionId;
    }

    public enum joinType {
        PLAYER,
        VIEWER
    }
}
