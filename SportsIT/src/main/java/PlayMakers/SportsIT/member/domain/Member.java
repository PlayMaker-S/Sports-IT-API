package PlayMakers.SportsIT.member.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.stereotype.Component;

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


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberType memberType;

}
