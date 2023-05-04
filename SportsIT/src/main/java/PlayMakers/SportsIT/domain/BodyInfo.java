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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    @JoinColumn(name = "memberId")
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
