package PlayMakers.SportsIT.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AgreementDto {
    private String agreementName;
    private String agreementUrl;
}
