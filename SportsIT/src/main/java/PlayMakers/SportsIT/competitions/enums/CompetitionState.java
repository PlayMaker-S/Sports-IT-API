package PlayMakers.SportsIT.competitions.enums;

import java.time.LocalDateTime;

public enum CompetitionState {
    PLANNING, // 계획중
    RECRUITING, // 모집중
    RECRUITING_END, // 모집 마감
    IN_PROGRESS, // 진행중
    END, // 종료 - 주최자가 대회 결과를 입력한 후
    CANCEL,
    ; // 중단

    public static CompetitionState getCompetitionState(LocalDateTime recruitingStart, LocalDateTime recruitingEnd, LocalDateTime startDate, LocalDateTime endDate) {
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(recruitingStart)) return PLANNING;
        if (now.isBefore(recruitingEnd)) return RECRUITING;
        if (now.isBefore(startDate)) return RECRUITING_END;
        if (now.isBefore(endDate)) return IN_PROGRESS;
        return END;
    }
}
