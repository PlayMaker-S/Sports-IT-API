package PlayMakers.SportsIT.member.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter @Setter
@Data
@Entity (name="member")
public class Member {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long uid;

    @Column(name = "id")
    private String id;

    @Column(name = "pw")
    private String pw;
    @Column(name = "name")
    private String name;

    public Member() {
    }

    public Member(String id, String pw, String name) {
        this.id = id;
        this.pw = pw;
        this.name = name;
    }

}
