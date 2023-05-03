package PlayMakers.SportsIT.domain;

import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity(name="bodyInfo")
public class BodyInfo {
    @Id
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uid")
    private Member member;
    @Column
    private float height;
    @Column
    private float weight;
    @Column
    private float smMass;
    @Column
    private float fatMass;

}
