package PlayMakers.SportsIT.dto;

import PlayMakers.SportsIT.domain.Competition;
import PlayMakers.SportsIT.domain.JoinCompetition;
import PlayMakers.SportsIT.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static PlayMakers.SportsIT.domain.JoinCompetition.*;

@Data
@AllArgsConstructor @NoArgsConstructor
@Builder
public class joinCompetitionDto {
    private Long uid;
    private Long competitionId;
    private joinType type;
    private String formId;
    private boolean isAgree;
    private boolean isPaid;

    public JoinCompetition toEntity() {
        return JoinCompetition.builder()
                .id(new JoinCompetitionId(uid, competitionId))
                .joinType(type)
                .formId(formId)
                .isAgree(isAgree)
                .isPaid(isPaid)
                .build();
    }
}
