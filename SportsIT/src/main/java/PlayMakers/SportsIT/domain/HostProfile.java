package PlayMakers.SportsIT.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity(name="hostProfile")
public class HostProfile extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long hostProfileId;

    @JsonIgnore
    @OneToOne(targetEntity = Member.class, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "uid")
    private Member member;

    @Column
    private String instagramUrl;

    @Column
    private String homepageUrl;

    @Column
    private String facebookUrl;

    @Column
    private String naverUrl;

    @Column
    private String youtubeUrl;

    @Column
    private String location;
}
