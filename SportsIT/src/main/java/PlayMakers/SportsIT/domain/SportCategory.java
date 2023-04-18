package PlayMakers.SportsIT.domain;

import lombok.Getter;

@Getter
public enum SportCategory {
    SOCCER("축구"),
    TENNIS("테니스"),
    BADMINTON("배드민턴"),
    GOLF("골프"),
    TABLE_TENNIS("탁구"),
    ARM_WRESTLING("팔씨름");
    private String name;
    private SportCategory(String name) {
        this.name = name;
    }
}