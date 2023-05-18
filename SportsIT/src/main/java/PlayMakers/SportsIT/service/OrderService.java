package PlayMakers.SportsIT.service;

import PlayMakers.SportsIT.domain.Member;
import PlayMakers.SportsIT.domain.Order;
import PlayMakers.SportsIT.dto.OrderDto;
import PlayMakers.SportsIT.dto.PaymentRequestDto;
import PlayMakers.SportsIT.dto.PaymentResponseDto;
import PlayMakers.SportsIT.enums.OrderStatus;
import PlayMakers.SportsIT.repository.OrderRepository;
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

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;

    @Value("${oneport.apikey}")
    private String impKey;
    @Value("${oneport.secret}")
    private String impSecret;

    public PaymentResponseDto paymentPreRecord(PaymentRequestDto paymentRequestDto) throws IOException{
        log.info("결제 내역 사전 등록");

        // access token 받아오기
        String token = getToken();
        log.info("token : {}", token);

        // 요청 생성
        HttpEntity<PaymentRequestDto> request = createOneportHttpRequestEntity(paymentRequestDto, token);

        // POST 요청 전송 & 수신
        String url = "https://api.iamport.kr/payments/prepare";
        ResponseEntity<PaymentResponseDto> response = new RestTemplate().postForEntity(url, request, PaymentResponseDto.class);
        PaymentResponseDto paymentResponseBody = response.getBody();
        log.info("response : {}", paymentResponseBody);

        // 응답 확인
        int code = Objects.requireNonNull(paymentResponseBody).getCode();
        if (code == -1) {
            log.info("이미 등록된 결제 내역");
            throw new IOException("결제 내역 사전 등록 실패");
        }

        return paymentResponseBody;
    }

    public boolean validate(PaymentRequestDto paymentRequestDto) throws IOException {
        log.info("결제 내역 사후 검증");

        // access token 받아오기
        String token = getToken();
        log.info("token : {}", token);

        // 요청 생성
        HttpEntity<PaymentRequestDto> request = createOneportHttpRequestEntity(paymentRequestDto, token);

        // GET 요청 전송 & 수신
        String url = "https://api.iamport.kr/payments/" + paymentRequestDto.getImp_uid();
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
        return isTampered(paymentRequestDto, expected_imp_uid, expected_merchant_uid, expected_amount);
    }

    public Order createOrder(OrderDto orderDto, Member member){
        log.info("결제 생성");

        Order newOrder = Order.builder()
                .impUid(orderDto.getImp_uid())
                .merchantUid(orderDto.getMerchant_uid())
                .price(orderDto.getPrice())
                .paymentType(orderDto.getPaymentType())
                .content(orderDto.getContent())
                .status(orderDto.getStatus())
                .status(OrderStatus.PAID)
                .buyer(member)
                .build();

        orderRepository.save(newOrder);

        return newOrder;
    }

    private static boolean isTampered(PaymentRequestDto paymentRequestDto, String expected_imp_uid, String expected_merchant_uid, Long expected_amount) {
        if(paymentRequestDto.getImp_uid().equals(expected_imp_uid)
                && paymentRequestDto.getMerchant_uid().equals(expected_merchant_uid)
                && paymentRequestDto.getAmount().equals(expected_amount)){
            log.info("결제 내역 검증 성공");
            return false;
        } else {
            log.info("결제 내역 검증 실패");
            return true;
        }
    }

    @NotNull
    private static HttpEntity<PaymentRequestDto> createOneportHttpRequestEntity(PaymentRequestDto paymentRequestDto, String token) {
        // 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", token);

        // 요청 생성
        HttpEntity<PaymentRequestDto> request = new HttpEntity<>(paymentRequestDto, headers);
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


}
