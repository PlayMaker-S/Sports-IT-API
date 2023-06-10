package PlayMakers.SportsIT.controller;

import PlayMakers.SportsIT.domain.Member;
import PlayMakers.SportsIT.domain.Payment;
import PlayMakers.SportsIT.dto.PaymentDto;
import PlayMakers.SportsIT.service.MemberService;
import PlayMakers.SportsIT.service.PaymentService;
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
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;
    private final MemberService memberService;

    @PostMapping("/record")
    public ResponseEntity<?> preValidation(@RequestBody PaymentDto.PreRequest preRequestDto,
                                           @AuthenticationPrincipal User user) throws Exception {
        log.info("사전 검증 호출 / 클라이언트: {}", user.getUsername());
        PaymentDto.Response response = paymentService.record(preRequestDto);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/complete")
    public ResponseEntity<String> complete(@RequestBody PaymentDto.Request requestDto,
                                      @AuthenticationPrincipal User user) throws Exception {
        log.info("결제이력 저장 / 클라이언트: {}", user.getUsername());
        log.info("imp_uid: {}", requestDto.getImp_uid());
        Member client = memberService.findOne(user.getUsername());

        // 결제 내역 사후 검증
        PaymentDto.PreRequest preRequestDto = requestDto.toPreRequest();
        boolean isValidPayment = paymentService.validate(preRequestDto);

        if(!isValidPayment) {
            return ResponseEntity.badRequest().body("결제 정보가 유효하지 않습니다.");
        }

        // 결제 내역 저장
        Payment payment = paymentService.createOrder(requestDto, client);

        if(payment != null) return ResponseEntity.ok("결제 내역 생성 완료");
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

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("IllegalArgumentException 발생", e);
        JsonObject response = new JsonObject();
        response.addProperty("code", "401");
        response.addProperty("message", e.getMessage());
        return ResponseEntity.badRequest().body(response.toString());
    }

}
