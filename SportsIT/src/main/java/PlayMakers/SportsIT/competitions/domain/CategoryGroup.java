package PlayMakers.SportsIT.competitions.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cat_group")
@Builder
@Getter
@NoArgsConstructor @AllArgsConstructor
public class CategoryGroup {
    @Id
    private Long code;
    @Column(length = 30)
    private String name;
}
