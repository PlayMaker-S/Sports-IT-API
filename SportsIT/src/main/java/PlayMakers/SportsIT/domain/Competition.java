package PlayMakers.SportsIT.domain;

import PlayMakers.SportsIT.enums.CompetitionType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// @AllArgsConstructor: 모든 필드값이 들어간 생성자 선언 @NoArgsConstructor : 이하 생략
@AllArgsConstructor @NoArgsConstructor
@Getter @Setter
@Builder
@DynamicInsert // null인 필드는
@Entity(name="competition") // 대회 엔티티 이름 지정
public class Competition extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) // MySQL에서는 AI 해제할 것
    private Long competitionId;
    @Column(nullable = false, length = 100)
    private String name; // 대회 이름
    @Builder.Default // Builder의 default 설정 : viewCount = 0;
    @Column(nullable = false)
    @ColumnDefault("0") // MySQL에서의 default 0으로 설정
    private Integer viewCount = 0; // 조회수
    @Builder.Default
    @Column(nullable = false)
    @ColumnDefault("0")
    private Integer scrapCount = 0; // 스크랩 수
    @Column(nullable = false)
    private LocalDateTime startDate; // 대회 시작일 - HH:MM:SS'T'YYYYMMDD -> 시간 기준이 뭔지 확인해 볼 필요
    @Column(nullable = false)
    private LocalDateTime endDate; // 대회 시작일 - HH:MM:SS'T'YYYYMMDD -> 시간 기준이 뭔지 확인해 볼 필요
    @Builder.Default
    @Column(nullable = false)
    private LocalDateTime recruitingStart = LocalDateTime.now(); // 모집 시작일
    @Column(nullable = false)
    private LocalDateTime recruitingEnd; // 모집 마감일
    @Builder.Default
    @Column(nullable = false)
    @ColumnDefault("0")
    private Integer totalPrize = 0; // 총 상금
    @Column(nullable = false, length=2000)
    private String content; // 대회 내용
    @Column(nullable = false, length = 100)
    private String location; // 대회 장소
    @Column(nullable = false, length = 100)
    private String locationDetail; // 대회 장소 상세
    @Column
    private Double latitude; // 위도
    @Column
    private Double longitude; // 경도
    @Builder.Default
    @Enumerated(EnumType.STRING)
    private CompetitionState state = CompetitionState.RECRUITING; // 대회 상태 - enum으로 구현했을 때

    @Column(length = 50)
    private String stateDetail; // 대회 상세 상태
    @Builder.Default
    @Enumerated(EnumType.STRING)
    private CompetitionType competitionType = CompetitionType.FREE; // 대회 타입 FREE, PREMIUM, VIP

    @ManyToOne(targetEntity = Member.class, fetch = FetchType.LAZY)
    @JoinTable(
            name = "competition_host",
            joinColumns = @JoinColumn(name = "competition_id"),
            inverseJoinColumns = @JoinColumn(name = "host_id"))
    @JsonIgnoreProperties({"pw", "email", "phone", "birth", "subscription", "activated", "authorities", "createdDate", "updatedDate", "memberType"})
    private Member host; // 주최자 uid

    @Enumerated(EnumType.STRING) // enum 타입을 DB에 저장할 때, enum의 이름을 저장하도록 설정
    private SportCategory category;

    @Column(length = 45)
    private String templateID; // 대회 신청폼 ID

    @Builder.Default
    @Column(scale = 3)
    @ColumnDefault("999")
    private Integer maxPlayer = 999; // 최대 참가자 수

    @Builder.Default
    @Column(scale = 3)
    @ColumnDefault("999")
    private Integer maxViewer = 999; // 최대 관람자 수

    @Builder.Default
    @OneToMany(mappedBy = "competition", cascade = CascadeType.ALL, orphanRemoval = true) // orphanRemoval : 대회 삭제 시, 대회 사진도 삭제, cascade : 대회 삭제 시, 대회 사진도 삭제
    private List<Poster> posters = new ArrayList<>(); // 대회 포스터 URL

    @Builder.Default
    @OneToMany(mappedBy = "competition", cascade = CascadeType.ALL, orphanRemoval = true) // orphanRemoval : 대회 삭제 시, 대회 규정도 삭제, cascade : 대회 삭제 시, 대회 규정도 삭제
    private List<Agreement> agreements = new ArrayList<>(); // 대회 규정

    @Scheduled(fixedDelay = 1000*60) // 1분마다 실행
    public void updateState() {
        LocalDateTime now = LocalDateTime.now();
        boolean isChanged = false;
        if (this.state == CompetitionState.PLANNING && this.recruitingStart.isBefore(now)) {
            this.state = CompetitionState.RECRUITING;
            isChanged = true;
        }
        if (this.state == CompetitionState.RECRUITING && this.recruitingEnd.isBefore(now)) {
            this.state = CompetitionState.RECRUITING_END;
            isChanged = true;
        }
        if (this.state == CompetitionState.RECRUITING_END && this.startDate.isBefore(now)) {
            this.state = CompetitionState.IN_PROGRESS;
            isChanged = true;
        }
        if (this.state == CompetitionState.IN_PROGRESS && this.endDate.isBefore(now)) {
            this.state = CompetitionState.END;
            isChanged = true;
        }
        //if (isChanged) competitionRepository.save(this);
    }
    @OneToMany(mappedBy = "competition")
    private List<CompetitionResult> competitionResults;

    @ManyToMany(targetEntity = Category.class, fetch = FetchType.LAZY)
    @JoinTable(
            name = "competition_category",
            joinColumns = {@JoinColumn(name = "competitionId")},
            inverseJoinColumns = {@JoinColumn(name = "category", referencedColumnName = "category")})
    private Set<Category> categories;

    @Override
    public String toString() {
        return "Competition{" +
                "competitionId=" + competitionId +
                ", name='" + name + '\'' +
                ", host=" + host.getUid() +
                ", category=" + category +
                ", viewCount=" + viewCount +
                ", scrapCount=" + scrapCount +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", recruitingStart=" + recruitingStart +
                ", recruitingEnd=" + recruitingEnd +
                ", totalPrize=" + totalPrize +
                ", content='" + content + '\'' +
                ", location='" + location + '\'' +
                ", locationDetail='" + locationDetail + '\'' +
                ", state=" + state +
                ", stateDetail='" + stateDetail + '\'' +
                ", competitionType=" + competitionType +
                ", templateID='" + templateID + '\'' +
                '}';
    }

}