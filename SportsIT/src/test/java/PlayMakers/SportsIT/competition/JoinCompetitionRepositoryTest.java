package PlayMakers.SportsIT.competition;

import PlayMakers.SportsIT.config.TestConfig;
import PlayMakers.SportsIT.domain.*;
import PlayMakers.SportsIT.repository.CompetitionRepository;
import PlayMakers.SportsIT.repository.JoinCompetitionRepository;
import PlayMakers.SportsIT.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.Collections;

@Slf4j
@ExtendWith(SpringExtension.class)  // 필요한 의존성 추가 (@Autowired, @MockBean)
@DataJpaTest  //@Transactional 어노테이션 포함 -> 테스트가 끝나면 자동으로 롤백
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestConfig.class)
public class JoinCompetitionRepositoryTest {
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    CompetitionRepository competitionRepository;
    @Autowired
    JoinCompetitionRepository joinCompetitionRepository;

    @BeforeEach
    public void before() {
        joinCompetitionRepository.deleteAll();
        competitionRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @Test
    @DisplayName("대회 신청")
    public void 대회신청() {
        // given
        Member host = Member.builder()
                .email("host@email.com")
                .name("host")
                .memberType(Collections.singleton(MemberType.builder().roleName("ROLE_INSTITUTION").build()))
                .pw("1234")
                .phone("010-1234-5678")
                .build();
        memberRepository.save(host);

        Member player = Member.builder()
                .email("player@email.com")
                .name("player")
                .memberType(Collections.singleton(MemberType.builder().roleName("ROLE_USER").build()))
                .pw("1234")
                .phone("010-1111-1111")
                .build();
        memberRepository.save(player);

        Member viewer = Member.builder()
                .email("viewer@email.com")
                .name("viewer")
                .memberType(Collections.singleton(MemberType.builder().roleName("ROLE_USER").build()))
                .pw("1234")
                .phone("010-2222-2222")
                .build();
        memberRepository.save(viewer);

        Competition competition = Competition.builder()
                .host(host)
                .category(SportCategory.SOCCER)
                .name("test")
                .content("test")
                .templateID("1")
                .location("test")
                .locationDetail("test")
                .recruitingStart(LocalDateTime.now())
                .recruitingEnd(LocalDateTime.now().plusDays(1))
                .startDate(LocalDateTime.now().plusDays(5))
                .endDate(LocalDateTime.now().plusDays(7))
                .build();
        competitionRepository.save(competition);

        // when
        JoinCompetition joinAsPlayer = JoinCompetition.builder()
                .competition(competition)
                .member(player)
                .id(new JoinCompetition.JoinCompetitionId(player.getUid(), competition.getCompetitionId()))
                .formId("1")
                .isAgree(true)
                .isPaid(true)
                .joinType(JoinCompetition.joinType.PLAYER)
                .build();

        joinCompetitionRepository.save(joinAsPlayer);

        JoinCompetition joinAsViewer = JoinCompetition.builder()
                .competition(competition)
                .member(viewer)
                .id(new JoinCompetition.JoinCompetitionId(viewer.getUid(), competition.getCompetitionId()))
                .formId("2")
                .isAgree(true)
                .isPaid(true)
                .joinType(JoinCompetition.joinType.VIEWER)
                .build();

        joinCompetitionRepository.save(joinAsViewer);

        // then
        log.info("대회신청자: {}", joinCompetitionRepository.findAll());
        log.info("선수: {}", player.getUid());
        log.info("참관: {}", viewer.getUid());
        log.info("대회: {}", competition.getCompetitionId());

        Assert.assertEquals(2, joinCompetitionRepository.findAll().size());
        for (JoinCompetition joinCompetition : joinCompetitionRepository.findAll()) {
            log.info("대회신청자: {}", joinCompetition);
            Assert.assertEquals(competition.getCompetitionId(), joinCompetition.getCompetition().getCompetitionId());
        }
        Assert.assertEquals(joinAsPlayer.getId().getUid(), joinCompetitionRepository.findByIdUidAndIdCompetitionId(player.getUid(), competition.getCompetitionId()).get().getId().getUid());
        Assert.assertEquals(joinAsViewer.getId().getUid(), joinCompetitionRepository.findByIdUidAndIdCompetitionId(viewer.getUid(), competition.getCompetitionId()).get().getId().getUid());
    }



}
