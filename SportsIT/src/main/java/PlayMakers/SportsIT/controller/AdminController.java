package PlayMakers.SportsIT.controller;

import PlayMakers.SportsIT.domain.Competition;
import PlayMakers.SportsIT.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Controller
@RequestMapping("/")
public class AdminController {
    @GetMapping("/")
    public String homeContent(){
        return "main";
    }
    @GetMapping("/member_admin")
    public String getMembers(Model model) {
        RestTemplate restTemplate = new RestTemplate();
        String apiUrl = "http://localhost:8080/api/member/all"; // 실제 API 엔드포인트

        // API 호출하여 데이터 가져오기
        Member[] members = restTemplate.getForObject(apiUrl, Member[].class);

        // 모델에 데이터 추가
        model.addAttribute("members", members);

        return "member"; // Thymeleaf 템플릿의 파일명
    }
    @GetMapping("/competition_admin")
    public String getCompetitions(Model model) {
        RestTemplate restTemplate = new RestTemplate();
        String apiUrl = "http://localhost:8080/api/competitions/all"; // 실제 API 엔드포인트

        // API 호출하여 데이터 가져오기
        Competition[] competitions = restTemplate.getForObject(apiUrl, Competition[].class);

        // 모델에 데이터 추가
        model.addAttribute("competitions", competitions);

        return "competition"; // Thymeleaf 템플릿의 파일명
    }
    @GetMapping("/payment_admin")
    public String paymentContent(){ return "payment"; }
}
