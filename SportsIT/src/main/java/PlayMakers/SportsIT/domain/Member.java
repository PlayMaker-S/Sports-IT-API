package PlayMakers.SportsIT.domain;

import PlayMakers.SportsIT.auth.security.userinfo.OAuth2UserInfo;
import PlayMakers.SportsIT.auth.security.enums.AuthProvider;
import PlayMakers.SportsIT.competitions.domain.Category;
import PlayMakers.SportsIT.enums.Subscribe;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Set;

@AllArgsConstructor @NoArgsConstructor
@Builder
@Getter @Setter
@Entity (name="member")
@JsonIgnoreProperties({"hibernateLazyInitializer", "pw"})
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

    @Column
    private String birth;
    @Column
    private String description;

    @Column
    private String profileImageUrl;

    @Column(nullable = false)
    @Builder.Default
    @Enumerated(EnumType.STRING)
    private Subscribe subscription = Subscribe.FREE;

    @Builder.Default
    @Column(nullable = false)
    private boolean activated = true;

    @Column
    private AuthProvider authProvider;

    @Column
    private String providerId;

    @ManyToMany
    @JoinTable(
            name = "member_authority",
            joinColumns = {@JoinColumn(name = "member_uid", referencedColumnName = "uid")},
            inverseJoinColumns = {@JoinColumn(name = "member_type", referencedColumnName = "member_type")})
    private Set<MemberType> memberType;

    @OneToMany(mappedBy = "member")
    private List<Feed> feeds;

    @OneToOne(mappedBy = "member")
    private HostProfile hostProfile;

    @OneToMany(mappedBy = "follower")
    @Builder.Default
    private Set<Follow> following = null;

    @OneToMany(mappedBy = "following")
    @Builder.Default
    private Set<Follow> followers = null;

    @JsonIgnoreProperties({"member"})
    @OneToMany(mappedBy = "member")
    private List<CompetitionResult> competitionResults;

    @ManyToMany
    @JoinTable(
            name = "member_category",
            joinColumns = {@JoinColumn(name = "member_uid", referencedColumnName = "uid")},
            inverseJoinColumns = {@JoinColumn(name = "cat_code", referencedColumnName = "code")})
    private Set<Category> categories;

    public Member update(OAuth2UserInfo oAuth2UserInfo) {
        this.name = oAuth2UserInfo.getName();
        this.providerId = oAuth2UserInfo.getOAuth2Id();

        return this;
    }
}
