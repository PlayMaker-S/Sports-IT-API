package PlayMakers.SportsIT.domain;

import PlayMakers.SportsIT.enums.OrderStatus;
import PlayMakers.SportsIT.enums.PaymentType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor @NoArgsConstructor
@Getter
@Entity(name = "orders")
public class Order extends BaseEntity{
    @Id
    String merchantUid;
    @Column(nullable = false)
    String impUid;
    @ManyToOne(targetEntity = Member.class, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(
            name = "member_order",
            joinColumns = {@JoinColumn(name = "merchant_uid")},
            inverseJoinColumns = {@JoinColumn(name = "buyer_uid")}
    )
    private Member buyer;

    @Column(nullable = false)
    String content; // 주문 내역에 대한 설명
    @Column(nullable = false)
    Long price;
    @Column(nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    PaymentType paymentType;
    @Column(nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    OrderStatus status;
    @Column
    String refundReason;

}
