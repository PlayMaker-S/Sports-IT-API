package PlayMakers.SportsIT.competition;

import PlayMakers.SportsIT.config.TestConfig;
import PlayMakers.SportsIT.domain.*;
import PlayMakers.SportsIT.dto.JoinCompetitionDto;
import PlayMakers.SportsIT.repository.CompetitionRepository;
import PlayMakers.SportsIT.repository.JoinCompetitionRepository;
import PlayMakers.SportsIT.repository.MemberRepository;
import PlayMakers.SportsIT.service.JoinCompetitionService;
import lombok.extern.slf4j.Slf4j;
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

import static PlayMakers.SportsIT.domain.JoinCompetition.*;

@Slf4j
@DataJpaTest
@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)  // 실제 DB로 테스트
@Import(TestConfig.class)
public class JoinCompetitionServiceIntegrationTest {
    @Autowired
    CompetitionRepository competitionRepository;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    JoinCompetitionRepository joinCompetitionRepository;

    MemberType userTypePlayer = MemberType.builder()
            .roleName("ROLE_USER")
            .build();
    MemberType userTypeInst = MemberType.builder()
            .roleName("ROLE_INSTITUTION")
            .build();

    @BeforeEach
    void Before() {
        joinCompetitionRepository.deleteAll();
    }

    @Test
    @DisplayName("체육인은 대회 참가를 신청할 수 있다.")
    void 대회_신청(){
        // given
        JoinCompetitionService joinCompetitionService = new JoinCompetitionService(competitionRepository, memberRepository, joinCompetitionRepository);

        int hostIdx = 1;
        MemberType hostMemberType = userTypeInst;
        Subscribe hostSubscription = Subscribe.BASIC_HOST;
        Member host = getMember(hostIdx, hostMemberType, hostSubscription);
        host = memberRepository.save(host);
        Long hostId = host.getUid();
        log.info("host: {}", host);

        LocalDateTime recruitingStart = LocalDateTime.now().minusDays(2);
        CompetitionState competitionState = CompetitionState.RECRUITING;
        Competition competition = getCompetition(host, recruitingStart, competitionState);
        competition = competitionRepository.save(competition);
        Long competitionId = competition.getCompetitionId();
        log.info("competition: {}", competition);

        // when
        Member player = getMember(2, userTypePlayer, Subscribe.BASIC_PLAYER);
        player = memberRepository.save(player);

        JoinCompetitionDto joinCompetitionDto = JoinCompetitionDto.builder()
                .competitionId(competitionId)
                .uid(player.getUid())
                .type(joinType.PLAYER)
                .formId("111")
                .build();

        for(Member member:memberRepository.findAll()){
            log.info("member: {}", member.getUid());
        }

        // then
        Assertions.assertDoesNotThrow(() -> joinCompetitionService.join(joinCompetitionDto));
        JoinCompetition joinCompetition = joinCompetitionRepository.findByIdUidAndIdCompetitionId(player.getUid(),competitionId).get();
        Assertions.assertEquals(competitionId, joinCompetition.getId().getCompetitionId());
        Assertions.assertEquals(player.getUid(), joinCompetition.getId().getUid());
    }

    private static Competition getCompetition(Member host, LocalDateTime recruitingStart, CompetitionState competitionState) {
        Competition competition = Competition.builder()
                .host(host)
                .name("test")
                .content("test")
                .recruitingStart(recruitingStart)
                .recruitingEnd(recruitingStart.plusDays(4))
                .startDate(recruitingStart.plusDays(7))
                .endDate(recruitingStart.plusDays(10))
                .state(competitionState)
                .category(SportCategory.SOCCER)
                .location("서울시 강남구")
                .locationDetail("강남구 논현동")
                .maxPlayer(80)
                .maxViewer(80)
                .competitionType(CompetitionType.FREE)
                .build();
        return competition;
    }

    private static Member getMember(int memberIdx, MemberType memberType, Subscribe subscribe) {
        Member host = Member.builder()
                .pw("1234")
                .name("홍길동")
                .email("test_host"+ memberIdx +"@email.com")
                .phone("010-1234"+memberIdx+"+678")
                .memberType(Collections.singleton(memberType))
                .subscription(subscribe)
                .build();
        return host;
    }
}
