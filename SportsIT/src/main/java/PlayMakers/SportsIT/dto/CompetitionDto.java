package PlayMakers.SportsIT.dto;

import PlayMakers.SportsIT.domain.Competition;
import PlayMakers.SportsIT.domain.CompetitionState;
import PlayMakers.SportsIT.domain.Member;
import PlayMakers.SportsIT.domain.SportCategory;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor @NoArgsConstructor
@Data // @ToString, @EqualsAndHashCode, @Getter, @Setter, @RequiredArgsConstructor
public class CompetitionDto {

    private Long competitionId;
    private String name;
    private Member host;
    private SportCategory sportCategory;
    private Integer viewCount;
    private Integer scrapCount;
    private LocalDateTime startDate;
    private LocalDateTime recruitingStart;
    private LocalDateTime recruitingEnd;
    private Integer totalPrize;
    private String content;
    private String location;
    private CompetitionState state;
    private String stateDetail;
    private String createdDate;
    private String updatedDate;

    public Competition toEntity() {
        return Competition.builder()
                .competitionId(competitionId)
                .name(name)
                .host(host)
                .category(sportCategory)
                .viewCount(viewCount)
                .scrapCount(scrapCount)
                .startDate(startDate)
                .recruitingStart(recruitingStart)
                .recruitingEnd(recruitingEnd)
                .totalPrize(totalPrize)
                .content(content)
                .location(location)
                .state(state)
                .stateDetail(stateDetail)
                .build();
    }
}
