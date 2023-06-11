package PlayMakers.SportsIT.dto;

import PlayMakers.SportsIT.domain.Competition;
import PlayMakers.SportsIT.domain.CompetitionResult;
import PlayMakers.SportsIT.domain.Member;
import PlayMakers.SportsIT.service.CompetitionService;
import PlayMakers.SportsIT.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
