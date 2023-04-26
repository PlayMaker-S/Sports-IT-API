package PlayMakers.SportsIT.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "member_type")
@Getter @Setter @Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberType {

    @Id
    @Column(name = "member_type", nullable = false)
    private String roleName;
    // commit test
}