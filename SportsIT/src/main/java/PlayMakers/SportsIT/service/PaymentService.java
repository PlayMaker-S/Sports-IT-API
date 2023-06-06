package PlayMakers.SportsIT.service;

import PlayMakers.SportsIT.domain.Member;
import PlayMakers.SportsIT.domain.Payment;
import PlayMakers.SportsIT.dto.PaymentDto;
import PlayMakers.SportsIT.enums.PaymentStatus;
import PlayMakers.SportsIT.repository.PaymentRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.shaded.gson.Gson;
import com.nimbusds.jose.shaded.gson.JsonObject;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;

    @Value("${oneport.apikey}")
    private String impKey;
    @Value("${oneport.secret}")
    private String impSecret;
    @Value("${oneport.imp.uid}")
    private String impUid;

    public PaymentDto.Response record(PaymentDto.PreRequest paymentDto) throws IOException, IllegalArgumentException{
        log.info("결제 내역 사전 등록");

        if(paymentDto.getImp_uid() == null) {
            String errMessage = "imp_uid가 null입니다.";
            log.error(errMessage);
            throw new IllegalArgumentException(errMessage);
        }
        if(!paymentDto.getImp_uid().equals(impUid)){
            String errMessage = "IMP_UID가 일치하지 않습니다.";
            log.error(errMessage);
            throw new IllegalArgumentException(errMessage);
        }

        // access token 받아오기
        String token = getToken();
        log.info("token : {}", token);

        paymentDto.setMerchant_uid(generateMerchatUid());

        // 요청 생성
        HttpEntity<PaymentDto.PreRequest> request = createOneportHttpRequestEntity(paymentDto, token);

        // POST 요청 전송 & 수신
        String url = "https://api.iamport.kr/payments/prepare";
        ResponseEntity<PaymentDto.Response> response = new RestTemplate().postForEntity(url, request, PaymentDto.Response.class);
        PaymentDto.Response paymentResponseBody = response.getBody();
        log.info("response : {}", paymentResponseBody);

        // 응답 확인
        int code = Objects.requireNonNull(paymentResponseBody).getCode();
        if (code == 1) {
            log.info("이미 등록된 결제 내역");
            throw new IOException("결제 내역 사전 등록 실패");
        }

        return paymentResponseBody;
    }

    public boolean validate(PaymentDto.PreRequest paymentDto) throws IOException {
        log.info("결제 내역 사후 검증");

        // access token 받아오기
        String token = getToken();
        log.info("token : {}", token);

        // 요청 생성
        HttpEntity<PaymentDto.PreRequest> request = createOneportHttpRequestEntity(paymentDto, token);

        // GET 요청 전송 & 수신
        String url = "https://api.iamport.kr/payments/" + paymentDto.getImp_uid();
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        String responseBody = response.getBody();
        log.info("response : {}", responseBody);

        // JSON 파싱
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(responseBody);

        // 필요한 속성 추출
        String expected_imp_uid = jsonNode.get("response").get("imp_uid").asText();
        String expected_merchant_uid = jsonNode.get("response").get("merchant_uid").asText();
        Long expected_amount = jsonNode.get("response").get("amount").asLong();

        // 검증
        return isTampered(paymentDto, expected_imp_uid, expected_merchant_uid, expected_amount);
    }

    public Payment createOrder(PaymentDto.Request requestDto, Member member){
        log.info("결제 생성");

        Payment newPayment = Payment.builder()
                .impUid(requestDto.getImp_uid())
                .merchantUid(requestDto.getMerchant_uid())
                .amount(requestDto.getAmount())
                .type(requestDto.getPaymentType())
                .content(requestDto.getContent())
                .status(requestDto.getStatus())
                .status(PaymentStatus.PAID)
                .buyer(member)
                .build();

        paymentRepository.save(newPayment);

        return newPayment;
    }

    private static boolean isTampered(PaymentDto.PreRequest paymentDto, String expected_imp_uid, String expected_merchant_uid, Long expected_amount) {
        if(paymentDto.getImp_uid().equals(expected_imp_uid)
                && paymentDto.getMerchant_uid().equals(expected_merchant_uid)
                && paymentDto.getAmount().equals(expected_amount)){
            log.info("결제 내역 검증 성공");
            return false;
        } else {
            log.info("결제 내역 검증 실패");
            return true;
        }
    }

    @NotNull
    private static HttpEntity<PaymentDto.PreRequest> createOneportHttpRequestEntity(PaymentDto.PreRequest paymentDto, String token) {
        // 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", token);

        // 요청 생성
        HttpEntity<PaymentDto.PreRequest> request = new HttpEntity<>(paymentDto, headers);
        return request;
    }

    private String getToken() throws IOException {
        /*
        * OnePort에 access token을 요청하는 API
         */

        URL url = new URL("https://api.iamport.kr/users/getToken");

        HttpsURLConnection conn = setHttpsURLConnection(url);
        JsonObject json = new JsonObject();

        json.addProperty("imp_key", impKey);
        json.addProperty("imp_secret", impSecret);

        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));

        bw.write(json.toString());
        bw.flush();
        bw.close();

        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));

        Gson gson = new Gson();

        String response = gson.fromJson(br.readLine(), Map.class).get("response").toString();
        String token = gson.fromJson(response, Map.class).get("access_token").toString();

        log.info("response : {}", response);

        br.close();
        conn.disconnect();

        return "Bearer " + token;
    }

    @NotNull
    private static HttpsURLConnection setHttpsURLConnection(URL url) throws IOException {
        HttpsURLConnection conn = null;
        conn = (HttpsURLConnection) url.openConnection();

        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-type", "application/json");
        conn.setRequestProperty("Accept", "application/json");
        conn.setDoOutput(true);
        return conn;
    }

    private String generateMerchatUid() {
        /*
        * merchant_uid 생성
        * */
        String merchantUid = "PAY" + UUID.randomUUID().toString().replaceAll("-", "");
        if(paymentRepository.existsById(merchantUid)){
            return generateMerchatUid();
        }
        return "PAY"+UUID.randomUUID().toString().replaceAll("-", "");
    }

}
