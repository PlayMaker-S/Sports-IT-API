package PlayMakers.SportsIT.domain;

import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

// @AllArgsConstructor: 모든 필드값이 들어간 생성자 선언 @NoArgsConstructor : 이하 생략
@AllArgsConstructor @NoArgsConstructor
@Builder
@Getter @Setter
@Entity(name="competition") // 대회 엔티티 이름 지정
public class Competition {
    @Id @GeneratedValue(strategy = GenerationType.AUTO) // MySQL에서는 AI 해제할 것
    private Long competitionId;
    private String host; // 주최자 email
    @Column(nullable = false)
    private String name; // 대회 이름
    @Column(nullable = false)
    private Integer sportsType; // 스포츠 종목 -> category
    @Column(nullable = false)
    private Integer viewCount; // 조회수
    @Column(nullable = false)
    private Integer scrapCount; // 스크랩 수
    @Column(nullable = false)
    private LocalDateTime startDate; // 대회 시작일 - HH:MM:SS'T'YYYYMMDD -> 시간 기준이 뭔지 확인해 볼 필요
    @Column(nullable = false)
    private LocalDateTime recruitingStart; // 모집 시작일
    @Column(nullable = false)
    private LocalDateTime recruitingEnd; // 모집 마감일
    @Column(nullable = false)
    private Integer totalPrize; // 총 상금
    @Column(nullable = false, length=2000)
    private String content; // 대회 내용
    @Column(nullable = false)
    private String location; // 대회 장소
    @Column(nullable = false)
    private CompetitionState state; // 대회 상태 - enum으로 구현했을 때
    private String stateDetail; // 대회 상세 상태
    @CreatedDate
    private String createdDate;  // dto에서는 제외해도 될듯
    @LastModifiedDate
    private String updatedDate;  // 마찬가지

    @ManyToOne(targetEntity = Member.class, fetch = FetchType.LAZY)
    @JoinTable(
            name = "COMPETITION_HOST",  // JPA에서 사용할 Join Table 이름 -> MySQL에서 확인시 competition_host 테이블이 생성됨
            joinColumns = {@JoinColumn(name = "COMPETITION", referencedColumnName = "competitionId")}, // 외래키
            inverseJoinColumns = {@JoinColumn(name = "HOST", referencedColumnName = "email")}) // 외래키의 참조키

    @JoinColumn(name="category", nullable = false)
    private SportCategory category;

}