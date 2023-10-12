package PlayMakers.SportsIT.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration // WebMvcConfigurer를 구현하는 클래스에 @Configuration을 붙여야 한다.
public class WebConfigure implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 모든 요청에 대해서
                .allowedOrigins("/*") // 모든 오리진 URL에 대해
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS") // 허용 메소드
                .allowedHeaders("Origin, Content-Type, Accept, X-Requested-With, Authorization") // 허용 헤더
                .allowCredentials(true); // 자격증명 허용
    }
}
