package PlayMakers.SportsIT.competition;

import PlayMakers.SportsIT.domain.*;
import PlayMakers.SportsIT.dto.CompetitionDto;
import PlayMakers.SportsIT.repository.CompetitionRepository;
import PlayMakers.SportsIT.repository.MemberRepository;
import PlayMakers.SportsIT.service.CompetitionService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)  // 실제 DB로 테스트
//@RunWith(SpringJUnit4ClassRunner.class)
@RunWith(SpringRunner.class)
class CompetitionServiceUnitTest {
    @Autowired
    CompetitionRepository competitionRepository;
    @Autowired
    MemberRepository memberRepository;

    MemberType userTypePlayer = MemberType.builder()
            .roleName("ROLE_INSTITUTION")
            .build();
    MemberType userTypeInst = MemberType.builder()
            .roleName("ROLE_INSTITUTION")
            .build();
    MemberType userTypeAdmin = MemberType.builder()
            .roleName("ROLE_INSTITUTION")
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
        CompetitionService competitionService = new CompetitionService(competitionRepository, memberRepository);

        for (Member member : memberRepository.findAll()) {
            System.out.println("member email = " + member.getEmail());
        }

        // given 호스트가 대회 생성
        Member host = memberRepository.findByEmail("host@gmail.com");
        // host가 널일 경우

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
                .state(CompetitionState.RECRUITING)
                .build();


        // when 대회 dto 생성
        Competition created = competitionService.createCompetition(dto);
        Competition saved = competitionRepository.findById(created.getCompetitionId()).get();

        // then
        assertEquals(created.getCompetitionId(), saved.getCompetitionId());


    }
}