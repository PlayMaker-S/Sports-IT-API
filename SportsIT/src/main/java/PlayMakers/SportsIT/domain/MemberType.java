package PlayMakers.SportsIT.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
}
