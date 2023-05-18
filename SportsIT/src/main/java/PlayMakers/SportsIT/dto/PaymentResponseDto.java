package PlayMakers.SportsIT.dto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor @NoArgsConstructor
public class PaymentResponseDto {
    private int code;
    private String message;
    private PaymentResponseJson response;

    @Data
    @AllArgsConstructor @NoArgsConstructor
    public class PaymentResponseJson {
        private String merchant_uid;
        private Long amount;
    }
}


