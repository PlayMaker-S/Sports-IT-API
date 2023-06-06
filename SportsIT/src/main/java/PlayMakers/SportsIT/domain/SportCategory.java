package PlayMakers.SportsIT.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.Optional;

@Getter
public enum SportCategory {
    SOCCER("축구"),
    TENNIS("테니스"),
    BADMINTON("배드민턴"),
    GOLF("골프"),
    TABLE_TENNIS("탁구"),
    ARM_WRESTLING("팔씨름"),
    VALLEYBALL("배구"),
    BOWLING("볼링"),
    ICE_HOCKEY("아이스하키"),
    HOCKEY("하키"),
    CANOE("카누"),
    YACHT("요트"),
    TAEKKYEON("택견"),
    SQUASH("스쿼시"),
    WATER_SKI("수상스키"),
    SEPAK_TAKRAW("세팍타크로"),
    SWIMMING("수영"),
    KARATE("카라테"),
    ICE_SPORTS("빙상"),
    RUGBY("럭비"),
    SSIREUM("씨름"),
    ROLLER("롤러"),
    GYMNASTICS("체조"),
    ROWING("조정"),
    SOFT_TENNIS("소프트테니스"),
    FIN_SWIMMING("수중핀수영"),
    BODYBUILDING("보디빌딩"),
    RUNNING("달리기"),
    FINGER_WRESTLING("손가락씨름"),
    MARATHON("마라톤"),
    JUMP_ROPE("줄넘기"),
    RACE_WALKING("걷기/경보");

    private String categoryName;

    SportCategory(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategory() {
        return categoryName;
    }

    public static Optional<SportCategory> fromCategoryName(String categoryName) {
        return Arrays.stream(values())
                .filter(category -> category.categoryName.equals(categoryName))
                .findFirst();
    }

}