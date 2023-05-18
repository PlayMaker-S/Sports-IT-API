package PlayMakers.SportsIT.dto;

import PlayMakers.SportsIT.domain.Order;
import PlayMakers.SportsIT.enums.OrderStatus;
import PlayMakers.SportsIT.enums.PaymentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor @NoArgsConstructor
public class OrderDto {
    private String merchant_uid;
    private String imp_uid;
    private String content;
    private Long price;
    private PaymentType paymentType;
    private OrderStatus status;
    private String refundReason;

    public PaymentRequestDto getPaymentRequestDto(OrderDto orderDto){
        return PaymentRequestDto.builder()
                .merchant_uid(merchant_uid)
                .amount(price)
                .build();
    }

    public Order toEntity(){
        return Order.builder()
                .merchantUid(merchant_uid)
                .impUid(imp_uid)
                .content(content)
                .price(price)
                .paymentType(paymentType)
                .status(status)
                .refundReason(refundReason)
                .build();
    }
}
