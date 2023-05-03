package PlayMakers.SportsIT.competition;

import PlayMakers.SportsIT.annotation.MainCompetitionPolicy;
import PlayMakers.SportsIT.config.TestConfig;
import PlayMakers.SportsIT.domain.*;
import PlayMakers.SportsIT.dto.CompetitionDto;
import PlayMakers.SportsIT.repository.CompetitionRepository;
import PlayMakers.SportsIT.repository.MemberRepository;

import PlayMakers.SportsIT.service.CompetitionService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@DataJpaTest
@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)  // 실제 DB로 테스트
@Import(TestConfig.class)
class CompetitionServiceIntegrationTest {
    @Autowired
    CompetitionRepository competitionRepository;
    @Autowired
    MemberRepository memberRepository;
    @MockBean  // @DataJpaTest에서는 JPA 관련 빈들만 등록되므로, @MockBean을 사용해야 한다.
    @MainCompetitionPolicy
    CompetitionPolicy competitionPolicy;

    MemberType userTypePlayer = MemberType.builder()
            .roleName("ROLE_PLAYER")
            .build();
    MemberType userTypeInst = MemberType.builder()
            .roleName("ROLE_INSTITUTION")
            .build();
    MemberType userTypeAdmin = MemberType.builder()
            .roleName("ROLE_ADMIN")
            .build();

    @BeforeEach
    public void before() {
        // 호스트 생성
        Member host = Member.builder()
                .pw("1234")
                .name("홍길동")
                .memberType(Collections.singleton(userTypeInst))
                .email("host@gmail.com")
                .phone("010-1234-5678")
                .build();

        System.out.println("host = " + host);
        memberRepository.save(host);
    }
    @Test
    public void 주최자대회생성() {
        CompetitionService competitionService = new CompetitionService(competitionRepository, memberRepository, competitionPolicy);

        // given 호스트가 대회 생성
        Member host = memberRepository.findByEmail("host@gmail.com");

        CompetitionDto dto = CompetitionDto.builder()
                .name("대회이름")
                .host(host)
                .sportCategory(SportCategory.ARM_WRESTLING)
                .viewCount(0)
                .scrapCount(0)
                .startDate(LocalDateTime.now())
                .recruitingStart(LocalDateTime.now())
                .recruitingEnd(LocalDateTime.now())
                .totalPrize(10000)
                .content("대회내용")
                .location("대회장소")
                .locationDetail("대회장소상세")
                .state(CompetitionState.RECRUITING)
                .competitionType(CompetitionType.FREE)
                .build();


        // when 대회 dto 생성
        Competition created = competitionService.create(dto);
        Competition saved = competitionRepository.findById(created.getCompetitionId()).get();

        // then
        assertEquals(created.getCompetitionId(), saved.getCompetitionId());
        log.info("created = {}", created.toString());

    }
    /*
        host의 구독정보, competition의 모집일/시작일 기준으로 동작하는 테스트 필요
     */
}