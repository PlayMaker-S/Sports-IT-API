package PlayMakers.SportsIT.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Data // getter, setter, toString, equals, hashCode 자동 생성
@NoArgsConstructor
public class CompetitionAgree {

    @EmbeddedId
    private AgreementId id;

    @MapsId("competitionId")  // PosterId.competitionId 매핑, 부모 테이블(Competition)의 PK를 FK 또는 복합키 칼럼으로 사용
    @ManyToOne(fetch = FetchType.LAZY)
    private Competition competition;

    @Column(nullable = false, length = 256)
    private String agreementUrl;

    @Embeddable
    public static class AgreementId implements Serializable {

        private static final long serialVersionUID = 1L;  //

        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "id")
        private Long id;

        @Column(name = "competition_id")
        private Long competitionId;

    }
}

