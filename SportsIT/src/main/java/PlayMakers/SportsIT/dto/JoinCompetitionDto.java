package PlayMakers.SportsIT.dto;

import PlayMakers.SportsIT.domain.JoinCompetition;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static PlayMakers.SportsIT.domain.JoinCompetition.*;

@Data
@AllArgsConstructor @NoArgsConstructor
@Builder
public class JoinCompetitionDto {
    private Long uid;
    private Long competitionId;
    private joinType type;
    private String formId;

    public JoinCompetition toEntity() {
        return JoinCompetition.builder()
                .id(new JoinCompetitionId(uid, competitionId))
                .joinType(type)
                .formId(formId)
                .build();
    }
}
