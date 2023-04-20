package PlayMakers.SportsIT.competition;

import PlayMakers.SportsIT.domain.*;
import PlayMakers.SportsIT.domain.Competition;
import PlayMakers.SportsIT.repository.CompetitionRepository;
import PlayMakers.SportsIT.repository.MemberRepository;
import org.junit.BeforeClass;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest  //@Transactional 어노테이션 포함 -> 테스트가 끝나면 자동으로 롤백
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ExtendWith(SpringExtension.class)  // 필요한 의존성 추가 (@Autowired, @MockBean)
class CompetitionRepositoryTest {
    @Autowired
    CompetitionRepository competitionRepository;

    @Autowired
    MemberRepository memberRepository;

    MemberType hostType = MemberType.builder()
            .roleName("ROLE_INSTITUTION")
            .build();

    @BeforeClass
    public void before() {
        // 호스트 생성
        Member member = Member.builder()
                .pw("1234")
                .name("홍길동")
                .memberType(Collections.singleton(hostType))
                .email("test99@gmail.com")
                .phone("010-1234-5678")
                .build();
        memberRepository.save(member);

        // 대회 생성
        Competition competition = Competition.builder()
                .name("테스트대회_0")
                .startDate(LocalDateTime.now())
                .recruitingStart(LocalDateTime.of(2023, 4, 20, 0, 0))
                .recruitingEnd(LocalDateTime.of(2023, 4, 25, 0, 0))
                .totalPrize(10000)
                .content("테스트 대회입니다.")
                .location("아주대학교")
                .category(SportCategory.SOCCER)
                .host(member)
                .build();
        competitionRepository.save(competition);
    }

    @Test
    void 대회저장() {
        //given
        Member host = Member.builder()
                .pw("1234")
                .name("홍길동")
                .memberType(Collections.singleton(hostType))
                .email("test99@gmail.com")
                .phone("010-1234-5678")
                .build();
        memberRepository.save(host);

        //when
        Competition competition = Competition.builder()
                .name("테스트대회")
                .startDate(LocalDateTime.now())
                .recruitingStart(LocalDateTime.of(2023, 4, 20, 0, 0))
                .recruitingEnd(LocalDateTime.of(2023, 4, 25, 0, 0))
                .totalPrize(10000)
                .content("테스트 대회입니다.")
                .location("서울시 서초구 반포대로 122")
                .category(SportCategory.SOCCER)
                .host(host)
                .build();
        competitionRepository.save(competition);

        //then
        Competition findCompetition = competitionRepository.findById(competition.getCompetitionId()).orElseThrow(() -> new IllegalArgumentException("해당 대회가 존재하지 않습니다."));
        assertThat(findCompetition).isEqualTo(competition);

    }
}

