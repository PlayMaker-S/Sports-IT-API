package PlayMakers.SportsIT.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor @NoArgsConstructor
public class PaymentRequestDto {
    private String merchant_uid;
    private String imp_uid;
    private Long amount;
}
