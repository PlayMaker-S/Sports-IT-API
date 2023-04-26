package PlayMakers.SportsIT.domain;

import java.time.LocalDateTime;

public interface CompetitionPolicy {
    /*
        Competition에 대한 정책 정의
     */
    CompetitionType getCompetitionType(Member host);
    CompetitionState getCompetitionState(Competition competition);
}
