package PlayMakers.SportsIT.domain;

import PlayMakers.SportsIT.enums.PaymentStatus;
import PlayMakers.SportsIT.enums.PaymentType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@AllArgsConstructor @NoArgsConstructor
@Builder
@Entity
public class Payment {
    @Id
    private String merchantUid;
    @Column(nullable = false)
    private String impUid;
    @Column
    private Long amount;
    @Column
    private String content;
    @ManyToOne(targetEntity = Member.class, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(
            name = "member_payment",
            joinColumns = {@JoinColumn(name = "merchant_uid")},
            inverseJoinColumns = {@JoinColumn(name = "buyer_uid")}
    )
    private Member buyer;
    @ManyToOne(targetEntity = Competition.class, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(
            name = "competition_payment",
            joinColumns = {@JoinColumn(name = "merchant_uid")},
            inverseJoinColumns = {@JoinColumn(name = "competition_id")}
    )
    private Competition competition;
    @Column(nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    private PaymentType type;
    @Column(nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    private String refundReason;

}
