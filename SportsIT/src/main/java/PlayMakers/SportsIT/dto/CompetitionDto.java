package PlayMakers.SportsIT.dto;

import PlayMakers.SportsIT.domain.*;
import PlayMakers.SportsIT.enums.CompetitionType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@AllArgsConstructor @NoArgsConstructor
@Data // @ToString, @EqualsAndHashCode, @Getter, @Setter, @RequiredArgsConstructor
public class CompetitionDto {

    private Long competitionId;
    private String name;
    private Member host;
    private SportCategory sportCategory;
    @Builder.Default
    private Integer viewCount = 0;
    @Builder.Default
    private Integer scrapCount = 0;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime recruitingStart;
    private LocalDateTime recruitingEnd;
    private Integer totalPrize;
    private String content;
    private String location;
    private String locationDetail;
    private CompetitionState state;
    private String stateDetail;
    private CompetitionType competitionType;
    private String templateId;
    @Builder.Default
    private Integer maxPlayer = 999;
    @Builder.Default
    private Integer maxViewer = 999;
    @Builder.Default
    private List<Poster> posters = new ArrayList<>();
    @Builder.Default
    private List<Agreement> agreements = new ArrayList<>();
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
                .endDate(endDate)
                .recruitingStart(recruitingStart)
                .recruitingEnd(recruitingEnd)
                .totalPrize(totalPrize)
                .content(content)
                .location(location)
                .locationDetail(locationDetail)
                .state(state)
                .stateDetail(stateDetail)
                .competitionType(competitionType)
                .templateID(templateId)
                .maxPlayer(maxPlayer)
                .maxViewer(maxViewer)
                .posters(posters)
                .agreements(agreements)
                .build();
    }
}
