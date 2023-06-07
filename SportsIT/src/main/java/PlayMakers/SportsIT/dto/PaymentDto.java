package PlayMakers.SportsIT.dto;

import PlayMakers.SportsIT.domain.Competition;
import PlayMakers.SportsIT.domain.Member;
import PlayMakers.SportsIT.domain.Payment;
import PlayMakers.SportsIT.enums.PaymentStatus;
import PlayMakers.SportsIT.enums.PaymentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class PaymentDto {
    @Data
    @AllArgsConstructor @NoArgsConstructor
    @Builder
    public static class PreRequest{
        private String merchant_uid;
        private String imp_uid;
        private Long amount;
    }
    @Data
    @AllArgsConstructor @NoArgsConstructor
    @Builder
    public static class Request{
        private String merchant_uid;
        private String imp_uid;
        private Long amount;
        private String content;
        private PaymentType paymentType;
        private PaymentStatus status;

        public PaymentDto.PreRequest toPreRequest(){
            return PreRequest.builder()
                    .merchant_uid(merchant_uid)
                    .amount(amount)
                    .build();
        }
    }
    @Data
    @AllArgsConstructor @NoArgsConstructor
    @Builder
    public static class Response{
        private int code;
        private String message;
        private ResponseJson response;

        @Data
        @AllArgsConstructor @NoArgsConstructor
        public class ResponseJson {
            private String merchant_uid;
            private Long amount;
        }
    }
    @Data
    @AllArgsConstructor @NoArgsConstructor
    @Builder
    public static class Detail{
        private String merchant_uid;
        private String imp_uid;
        private String content;
        private Long amount;
        private PaymentType paymentType;
        private PaymentStatus status;
        private String refundReason;

        public Payment toEntityWithMemberAndCompetition(Member member, Competition competition){
            return Payment.builder()
                    .merchantUid(merchant_uid)
                    .impUid(imp_uid)
                    .content(content)
                    .amount(amount)
                    .type(paymentType)
                    .status(status)
                    .refundReason(refundReason)
                    .buyer(member)
                    .competition(competition)
                    .build();
        }
    }

}
