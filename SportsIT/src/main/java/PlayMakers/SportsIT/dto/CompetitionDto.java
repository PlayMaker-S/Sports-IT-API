package PlayMakers.SportsIT.dto;

import PlayMakers.SportsIT.domain.CompetitionState;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Getter @Setter
@Builder
@AllArgsConstructor @NoArgsConstructor
public class CompetitionDto {

    private Long competitionId;
    private String name;
    private String hostEmail;
    private Integer sportsType;
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
}
