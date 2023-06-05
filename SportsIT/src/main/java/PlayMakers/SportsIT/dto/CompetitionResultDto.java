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

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CompetitionResultDto {
    private Competition competition;
    private Member member;
    private String content;
    private LocalDateTime historyDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public CompetitionResult toEntity(){

        return CompetitionResult.builder()
                .content(content)
                .competition(competition)
                .member(member)
                .historyDate(historyDate)
                .build();
    }
}
