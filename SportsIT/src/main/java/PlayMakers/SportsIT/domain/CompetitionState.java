package PlayMakers.SportsIT.domain;

public enum CompetitionState {
    PLANNING, // 계획중
    RECRUITING, // 모집중
    RECRUITING_END, // 모집 마감
    IN_PROGRESS, // 진행중
    END, // 종료 - 주최자가 대회 결과를 입력한 후
    CANCEL, // 중단
    EARLY_END // 조기 종료
}
