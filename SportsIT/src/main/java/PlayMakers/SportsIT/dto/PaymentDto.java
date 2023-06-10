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
        private String paymentType;
        private String status;

        public PaymentDto.PreRequest toPreRequest(){
            return PreRequest.builder()
                    .imp_uid(imp_uid)
                    .merchant_uid(merchant_uid)
                    .amount(amount)
                    .build();
        }

        public PaymentStatus getStatusEnum() {
            if (status == null) return null;
            else if (status.equals("paid")) return PaymentStatus.PAID;
            else if (status.equals("ready")) return PaymentStatus.READY;
            else if (status.equals("failed")) return PaymentStatus.FAILED;
            else if (status.equals("cancelled")) return PaymentStatus.CANCELLED;
            else throw new IllegalArgumentException("Invalid PaymentStatus: " + status);
        }
        public PaymentType getPaymentTypeEnum() {
            if (paymentType == null) return null;
            else if (paymentType.equals("card")) return PaymentType.CARD;
            else if (paymentType.equals("transfer")) return PaymentType.TRANSFER;
            else if (paymentType.equals("vbank")) return PaymentType.VIRTUAL_ACCOUNT;
            else if (paymentType.equals("phone")) return PaymentType.PHONE;
            else if (paymentType.equals("samsungpay")) return PaymentType.SAMSUNG;
            else if (paymentType.equals("kakaopay")) return PaymentType.KAKAO;
            else if (paymentType.equals("payco")) return PaymentType.PAYCO;
            else if (paymentType.equals("toss")) return PaymentType.TOSS;
            else if (paymentType.equals("paypal")) return PaymentType.PAYPAL;
            else throw new IllegalArgumentException("Invalid PaymentType: " + paymentType);
        }
        @Override
        public String toString() {
            return "Request{" +
                    "merchant_uid='" + merchant_uid + '\'' +
                    ", imp_uid='" + imp_uid + '\'' +
                    ", amount=" + amount +
                    ", content='" + content + '\'' +
                    ", paymentType='" + paymentType + '\'' +
                    ", status='" + status + '\'' +
                    '}';
        }
    }
    @Data
    @AllArgsConstructor @NoArgsConstructor
    @Builder
    public static class PortOneResponse {
        private String code;
        private String message;
        private ResponseJson response;
        private String status;
        private String pg_provider;

        @Data
        @AllArgsConstructor @NoArgsConstructor
        public class ResponseJson {
            private String merchant_uid;
            private Long amount;

            @Override
            public String toString() {
                return "ResponseJson{" +
                        "merchant_uid='" + merchant_uid + '\'' +
                        ", amount=" + amount +
                        '}';
            }
        }

        @Override
        public String toString() {
            return "PortOneResponse{" +
                    "code='" + code + '\'' +
                    ", message='" + message + '\'' +
                    ", response=" + response.toString() +
                    '}';
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
