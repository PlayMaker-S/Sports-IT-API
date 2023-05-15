package PlayMakers.SportsIT.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Entity
@Data // getter, setter, toString, equals, hashCode 자동 생성
@Builder
@NoArgsConstructor @AllArgsConstructor
public class Poster {

    @Id
    private String posterUrl;

    @JsonIgnore
    @JoinColumn(name = "competition_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Competition competition;

}
