package PlayMakers.SportsIT.service;

import PlayMakers.SportsIT.domain.Competition;
import PlayMakers.SportsIT.domain.CompetitionState;
import PlayMakers.SportsIT.domain.Member;
import PlayMakers.SportsIT.domain.MemberType;
import PlayMakers.SportsIT.dto.CompetitionDto;
import PlayMakers.SportsIT.repository.CompetitionRepository;
import PlayMakers.SportsIT.repository.MemberRepository;
import org.junit.Before;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest  //@Transactional 어노테이션 포함 -> 테스트가 끝나면 자동으로 롤백
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ExtendWith(SpringExtension.class)  // 필요한 의존성 추가 (@Autowired, @MockBean)
class CompetitionServiceTest {
    CompetitionRepository competitionRepository;
    MemberRepository memberRepository;
    CompetitionService service;

    MemberType userTypePlayer = MemberType.builder()
            .roleName("ROLE_INSTITUTION")
            .build();
    MemberType userTypeInst = MemberType.builder()
            .roleName("ROLE_INSTITUTION")
            .build();
    MemberType userTypeAdmin = MemberType.builder()
            .roleName("ROLE_INSTITUTION")
            .build();

    @Before
    void createHost() {
        // 레포지토리 삭제
        memberRepository.deleteAll();

        // 호스트 생성
        Member host = Member.builder()
                .loginId("host@gmail.com")
                .pw("1234")
                .name("홍길동")
                .memberType(Collections.singleton(userTypeInst))
                .email("host@gmail.com")
                .phone("010-1234-5678")
                .build();

        memberRepository.save(host);
    }

    @Nested
    @DisplayName("대회 생성 테스트")
    class OpenCompetition {
        @Test
        void 주최자대회생성() {
            // given 호스트가 대회 생성
            Member host = memberRepository.findByLoginId("host@gmail.com");

            CompetitionDto dto = CompetitionDto.builder()
                    .name("대회이름")
                    .hostEmail(host.getEmail())
                    .sportsType(1)
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
            Competition created = service.createCompetition(dto);

            Competition saved = competitionRepository.findById(created.getCompetitionId()).get();

            // then
            assertEquals(created.getCompetitionId(), saved.getCompetitionId());


        }
    }
}