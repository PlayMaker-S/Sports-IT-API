package PlayMakers.SportsIT.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor @AllArgsConstructor
public class Agreement {
    @Id
    private String agreementUrl;
    @Column(nullable = false)
    private String name;

    @JsonIgnore
    @JoinColumn(name = "competition_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Competition competition;

}
