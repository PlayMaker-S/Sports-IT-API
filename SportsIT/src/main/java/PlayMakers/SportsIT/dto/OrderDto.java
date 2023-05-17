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
    private String merchantUid;
    private String impUid;
    private String content;
    private int price;
    private PaymentType paymentType;
    private OrderStatus status;
    private String refundReason;

    public Order toEntity(){
        return Order.builder()
                .merchantUid(merchantUid)
                .impUid(impUid)
                .content(content)
                .price(price)
                .paymentType(paymentType)
                .status(status)
                .refundReason(refundReason)
                .build();
    }
}
