package PlayMakers.SportsIT.domain;

import PlayMakers.SportsIT.enums.NotificationTitle;
import PlayMakers.SportsIT.enums.NotificationType;
import jakarta.persistence.*;
import lombok.*;
import org.checkerframework.checker.units.qual.C;

@Entity
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class Notification extends BaseEntity{
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private NotificationTitle title;
    @Column(nullable = false, length = 100)
    private String link;
    @Column(nullable = false, length = 80)
    private String message;
    @Column(nullable = false)
    @Builder.Default
    private boolean checked = false;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, targetEntity = Member.class, optional = false)
    @JoinColumn(name = "member_uid")
    private Member member;

    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;

    public void read() {
        this.checked = true;
    }

}
