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
    ARM_WRESTLING("팔씨름");

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