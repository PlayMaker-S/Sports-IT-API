package PlayMakers.SportsIT.dto;

import PlayMakers.SportsIT.domain.BodyInfo;
import PlayMakers.SportsIT.domain.Member;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data // @ToString, @EqualsAndHashCode, @Getter, @Setter, @RequiredArgsConstructor
public class BodyInfoDto {
    private Member member;
    private float height;
    private float weight;
    private float smMass;
    private float fatMass;
    public BodyInfo toEntity() {
        return BodyInfo.builder()
                .member(member)
                .height(height)
                .weight(weight)
                .smMass(smMass)
                .fatMass(fatMass)
                .build();
    }
}
