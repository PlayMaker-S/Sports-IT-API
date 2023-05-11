package PlayMakers.SportsIT.domain;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Getter @Setter
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

    @Embeddable
    @AllArgsConstructor @NoArgsConstructor
    @Getter @Setter
    public static class JoinCompetitionId implements Serializable {
        private Long uid;
        private Long competitionId;
    }

    public enum joinType {
        PLAYER,
        VIEWER
    }

    public void update(joinType type, String formId) {
        this.joinType = type;
        this.formId = formId;
    }
    @Override
    public String toString(){
        return "JoinCompetition{" +
                "id=" + id +
                ", member=" + member +
                ", competition=" + competition +
                ", joinType=" + joinType +
                ", formId='" + formId + '\'' +
                '}';
    }
}
