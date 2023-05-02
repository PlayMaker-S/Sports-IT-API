package PlayMakers.SportsIT.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuerydslConfig {
    @PersistenceContext // EntityManager를 빈으로 주입시 사용하는 어노테이션 (@Autowired와 비슷)
    private EntityManager em;

    @Bean
    public JPAQueryFactory japQueryFactory() {
        return new JPAQueryFactory(em);
        //return null;
    }
}
