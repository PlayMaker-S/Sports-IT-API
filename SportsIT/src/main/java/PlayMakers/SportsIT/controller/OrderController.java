package PlayMakers.SportsIT.controller;

import PlayMakers.SportsIT.domain.Member;
import PlayMakers.SportsIT.domain.Order;
import PlayMakers.SportsIT.dto.OrderDto;
import PlayMakers.SportsIT.dto.PaymentRequestDto;
import PlayMakers.SportsIT.dto.PaymentResponseDto;
import PlayMakers.SportsIT.service.MemberService;
import PlayMakers.SportsIT.service.OrderService;
import com.nimbusds.jose.shaded.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final MemberService memberService;

    @PostMapping("/record")
    public ResponseEntity<?> preValidation(@RequestBody PaymentRequestDto paymentRequestDto,
                                           @AuthenticationPrincipal User user) throws Exception {
        log.info("사전 검증 호출 / 클라이언트: {}", user.getUsername());
        PaymentResponseDto response = orderService.paymentPreRecord(paymentRequestDto);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/complete")
    public ResponseEntity<String> complete(@RequestBody OrderDto orderDto,
                                      @AuthenticationPrincipal User user) throws Exception {
        log.info("결제이력 저장 / 클라이언트: {}", user.getUsername());
        Member client = memberService.findOne(user.getUsername());

        // 결제 내역 사후 검증
        PaymentRequestDto paymentRequestDto = orderDto.getPaymentRequestDto(orderDto);
        boolean isValidPayment = orderService.validate(paymentRequestDto);

        if(!isValidPayment) {
            return ResponseEntity.badRequest().body("결제 정보가 유효하지 않습니다.");
        }

        // 결제 내역 저장
        Order order = orderService.createOrder(orderDto, client);

        if(order != null) return ResponseEntity.ok("결제 내역 생성 완료");
        else return ResponseEntity.internalServerError().body("결제 내역 생성 실패");
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<String> handleIOException(IOException e) {
        log.error("IOException 발생", e);
        JsonObject response = new JsonObject();
        response.addProperty("code", "401");
        response.addProperty("message", "이미 등록된 결제 정보입니다.");
        return ResponseEntity.badRequest().body(response.toString());
    }

}
