package PlayMakers.SportsIT.domain;

import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

// @AllArgsConstructor: 모든 필드값이 들어간 생성자 선언 @NoArgsConstructor : 이하 생략
@AllArgsConstructor @NoArgsConstructor
@Getter @Setter
@Builder
@DynamicInsert // null인 필드는
@Entity(name="competition") // 대회 엔티티 이름 지정
public class Competition extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) // MySQL에서는 AI 해제할 것
    private Long competitionId;
    @Column(nullable = false)
    private String name; // 대회 이름
    @Builder.Default
    @Column(nullable = false)
    @ColumnDefault("0")
    private Integer viewCount = 0; // 조회수
    @Builder.Default
    @Column(nullable = false)
    @ColumnDefault("0")
    private Integer scrapCount = 0; // 스크랩 수
    @Column(nullable = false)
    private LocalDateTime startDate; // 대회 시작일 - HH:MM:SS'T'YYYYMMDD -> 시간 기준이 뭔지 확인해 볼 필요
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
    @Column(nullable = false)
    private String location; // 대회 장소
    @Builder.Default
    @Enumerated(EnumType.ORDINAL)
    private CompetitionState state = CompetitionState.RECRUITING; // 대회 상태 - enum으로 구현했을 때
    @Column
    private String stateDetail; // 대회 상세 상태

    @ManyToOne(targetEntity = Member.class, fetch = FetchType.LAZY)
    @JoinTable(
            name = "competition_host",
            joinColumns = @JoinColumn(name = "competition_id"),
            inverseJoinColumns = @JoinColumn(name = "host_id"))
    private Member host; // 주최자 uid

    @Enumerated(EnumType.STRING) // enum 타입을 DB에 저장할 때, enum의 이름을 저장하도록 설정
    private SportCategory category;


}