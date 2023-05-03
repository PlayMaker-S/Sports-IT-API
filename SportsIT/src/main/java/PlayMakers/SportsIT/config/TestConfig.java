package PlayMakers.SportsIT.config;

import PlayMakers.SportsIT.repository.CompetitionCustomRepositoryImpl;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestConfig {

    @PersistenceContext
    private EntityManager entityManager;

    @Bean
    public JPAQueryFactory jpaQueryFactory(){
        return new JPAQueryFactory(entityManager);
    }

    @Bean
    public CompetitionCustomRepositoryImpl competitionCustomRepository(){
        return new CompetitionCustomRepositoryImpl(jpaQueryFactory());
    }

}
