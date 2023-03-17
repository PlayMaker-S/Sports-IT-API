package PlayMakers.SportsIT.member.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.stereotype.Component;

import java.util.Set;

@AllArgsConstructor @NoArgsConstructor
@Builder
@Getter @Setter
@Entity (name="member")
public class Member {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long uid;

    @Column(nullable = false, unique = true)
    private String loginId;

    @Column(nullable = false)
    private String pw;

    @Column(nullable = false)
    private String name;

    @Column
    private String email;

    @Column
    private String phone;

    @JsonIgnore
    @Column
    private boolean activated;

    @ManyToMany
    @JoinTable(
            name = "member_authority",
            joinColumns = {@JoinColumn(name = "member_uid", referencedColumnName = "uid")},
            inverseJoinColumns = {@JoinColumn(name = "member_type", referencedColumnName = "member_type")})
    private Set<MemberType> memberType;

}
