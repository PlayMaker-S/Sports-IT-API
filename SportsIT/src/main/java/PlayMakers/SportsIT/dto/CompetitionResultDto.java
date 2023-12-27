package PlayMakers.SportsIT.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CompetitionResultDto {
    private Long competitionId;
    private Long uid;
    private String content;
    private LocalDateTime historyDate;
    private String name;
}
