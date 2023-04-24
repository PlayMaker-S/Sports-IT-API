package PlayMakers.SportsIT.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.Set;

@AllArgsConstructor @NoArgsConstructor
@Builder
@Getter @Setter
@Entity (name="member")
public class Member extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long uid;
    @Column(nullable = false)
    private String pw;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String phone;

    @Builder.Default
    @Column(nullable = false)
    private boolean activated = true;

    @ManyToMany
    @JoinTable(
            name = "member_authority",
            joinColumns = {@JoinColumn(name = "member_uid", referencedColumnName = "uid")},
            inverseJoinColumns = {@JoinColumn(name = "member_type", referencedColumnName = "member_type")})
    private Set<MemberType> memberType;

}
