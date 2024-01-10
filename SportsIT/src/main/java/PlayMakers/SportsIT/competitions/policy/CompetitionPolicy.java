package PlayMakers.SportsIT.competitions.policy;

import PlayMakers.SportsIT.competitions.domain.Competition;
import PlayMakers.SportsIT.competitions.enums.CompetitionState;
import PlayMakers.SportsIT.domain.Member;
import PlayMakers.SportsIT.enums.CompetitionType;

public interface CompetitionPolicy {
    /*
        Competition에 대한 정책 정의
     */
    CompetitionType getCompetitionType(Member host);
    CompetitionState getCompetitionState(Competition competition);
}
