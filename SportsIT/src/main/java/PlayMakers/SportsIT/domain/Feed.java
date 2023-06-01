package PlayMakers.SportsIT.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity(name="feed")
public class Feed extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long feedId;

    @JsonIgnore
    @ManyToOne(targetEntity = Member.class, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "uid")
    private Member member;

    @Column
    private String content;

    @Column
    private String imgUrl;

    @Column
    private int likeCnt;
}
