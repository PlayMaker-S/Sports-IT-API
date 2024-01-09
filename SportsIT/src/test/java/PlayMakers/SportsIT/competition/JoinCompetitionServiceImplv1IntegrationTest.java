package PlayMakers.SportsIT.competition;

import PlayMakers.SportsIT.competitions.domain.Competition;
import PlayMakers.SportsIT.config.TestConfig;
import PlayMakers.SportsIT.domain.*;
import PlayMakers.SportsIT.dto.JoinCompetitionDto;
import PlayMakers.SportsIT.enums.CompetitionType;
import PlayMakers.SportsIT.enums.Subscribe;
import PlayMakers.SportsIT.exceptions.RequestDeniedException;
import PlayMakers.SportsIT.exceptions.UnAuthorizedException;
import PlayMakers.SportsIT.repository.*;
import PlayMakers.SportsIT.service.JoinCompetitionService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

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
    @Autowired
    CompetitionCustomRepository competitionCustomRepository;
    @Autowired
    ParticipantRepository participantRepository;

    MemberType userTypePlayer = MemberType.builder()
            .roleName("ROLE_USER")
            .build();
    MemberType userTypeInst = MemberType.builder()
            .roleName("ROLE_INSTITUTION")
            .build();

    @BeforeEach
    void Before() {
        joinCompetitionRepository.deleteAll();
        competitionRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @Nested
    @DisplayName("체육인은 대회에 참가할 수 있다.")
    class 체육인_대회_참가 {
        @Test
        @DisplayName("체육인은 대회 참가를 신청할 수 있다.")
        void 대회_신청(){
            // given
            JoinCompetitionService joinCompetitionService = new JoinCompetitionService(competitionRepository, competitionCustomRepository, memberRepository, joinCompetitionRepository, participantRepository);

            int hostIdx = 1;
            MemberType hostMemberType = userTypeInst;
            Member host = getMember(hostIdx, hostMemberType, Subscribe.BASIC_HOST);
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
        @Test
        @DisplayName("체육인은 이미 참가한 대회에 참가할 수 없다.")
        void 이미_참가한_대회() {
            // given
            Member host = memberRepository.save(getMember(1, userTypeInst, Subscribe.BASIC_HOST));
            Competition recruitingCompetition = competitionRepository.save(getCompetition(host, LocalDateTime.now().minusDays(2), CompetitionState.RECRUITING));

//            Competition recruitEndCompetition = getCompetition(host, LocalDateTime.now().minusDays(-5), CompetitionState.RECRUITING_END);
//            recruitEndCompetition.setCompetitionId(2L);
            JoinCompetitionService joinCompetitionService = new JoinCompetitionService(competitionRepository, competitionCustomRepository, memberRepository, joinCompetitionRepository, participantRepository);
            Member player = getMember(2, userTypePlayer, Subscribe.BASIC_PLAYER);
            memberRepository.save(player);
            // when
            joinCompetitionService.join(JoinCompetitionDto.builder()
                    .competitionId(recruitingCompetition.getCompetitionId())
                    .uid(player.getUid())
                    .type(joinType.PLAYER)
                    .formId("111")
                    .build());

            JoinCompetitionDto joinCompetitionDto = JoinCompetitionDto.builder()
                    .competitionId(recruitingCompetition.getCompetitionId())
                    .uid(player.getUid())
                    .type(joinType.PLAYER)
                    .formId("111")
                    .build();

            // then
            Assertions.assertThrows(IllegalArgumentException.class, () -> joinCompetitionService.join(joinCompetitionDto));
        }
        @Test
        @DisplayName("모집중인 대회의 여석을 확인할 수 있다.")
        void 남은_선수_여석_확인() {
            // given (선행 조건 설정)
            Member host = memberRepository.save(getMember(1, userTypeInst, Subscribe.BASIC_HOST));
            Competition recruitingCompetition =
                    competitionRepository.save(getCompetition(host, LocalDateTime.now().minusDays(2), CompetitionState.RECRUITING));
            JoinCompetitionService joinCompetitionService =
                    new JoinCompetitionService(competitionRepository, competitionCustomRepository, memberRepository, joinCompetitionRepository, participantRepository);
            Member player = getMember(2, userTypePlayer, Subscribe.BASIC_PLAYER);
            memberRepository.save(player);

            // when (테스트할 데이터 입력 및 메소드 실행)
            joinCompetitionService.join(JoinCompetitionDto.builder()
                    .competitionId(recruitingCompetition.getCompetitionId())
                    .uid(player.getUid())
                    .type(joinType.PLAYER)
                    .formId("111")
                    .build());

            // then (테스트 결과 확인)
            try {
                Map<String, String> joinCounts = joinCompetitionService.getJoinCounts(recruitingCompetition.getCompetitionId(), player);
                Assertions.assertEquals("79", joinCounts.get("availablePlayer"));
                Assertions.assertEquals("80", joinCounts.get("availableViewer"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        @Test
        @DisplayName("체육인은 모집중이 아닌 대회에 참가할 수 없다.")
        void 모집중이_아닌_대회() {
            // given
            Member host = memberRepository.save(getMember(1, userTypeInst, Subscribe.BASIC_HOST));
            Competition recruitEndCompetition = competitionRepository.save(getCompetition(host, LocalDateTime.now().minusDays(5), CompetitionState.RECRUITING_END));
            JoinCompetitionService joinCompetitionService = new JoinCompetitionService(competitionRepository, competitionCustomRepository, memberRepository, joinCompetitionRepository, participantRepository);
            Member player = getMember(2, userTypePlayer, Subscribe.BASIC_PLAYER);
            memberRepository.save(player);
            // when
            JoinCompetitionDto joinCompetitionDto = JoinCompetitionDto.builder()
                    .competitionId(recruitEndCompetition.getCompetitionId())
                    .uid(player.getUid())
                    .type(joinType.PLAYER)
                    .formId("111")
                    .build();

            // then
            Assertions.assertThrows(RequestDeniedException.class, () -> joinCompetitionService.join(joinCompetitionDto));
        }
        @Test
        @DisplayName("주최자는 대회에 참가할 수 없다.")
        void 주최자_참가() {
            // given
            Member host = memberRepository.save(getMember(2, userTypeInst, Subscribe.BASIC_HOST));
            Competition recruitingCompetition = competitionRepository.save(getCompetition(host, LocalDateTime.now().minusDays(2), CompetitionState.RECRUITING));
            JoinCompetitionService joinCompetitionService = new JoinCompetitionService(competitionRepository, competitionCustomRepository, memberRepository, joinCompetitionRepository, participantRepository);
            // when
            JoinCompetitionDto joinCompetitionDto = JoinCompetitionDto.builder()
                    .competitionId(recruitingCompetition.getCompetitionId())
                    .uid(host.getUid())
                    .type(joinType.PLAYER)
                    .formId("111")
                    .build();

            // then
            Assertions.assertThrows(UnAuthorizedException.class, () -> joinCompetitionService.join(joinCompetitionDto));
        }
    }

    @Nested
    @DisplayName("체육인은 대회 참가를 취소할 수 있다.")
    class 대회_참가_취소 {
        @Test
        @DisplayName("대회 참가를 취소하면 참가자 목록에서 삭제된다.")
        void 대회_참가_취소() {
            // given
            Member host = memberRepository.save(getMember(1, userTypeInst, Subscribe.BASIC_HOST));
            Competition recruitingCompetition = competitionRepository.save(getCompetition(host, LocalDateTime.now().minusDays(2), CompetitionState.RECRUITING));
            JoinCompetitionService joinCompetitionService = new JoinCompetitionService(competitionRepository, competitionCustomRepository, memberRepository, joinCompetitionRepository, participantRepository);
            Member player = getMember(2, userTypePlayer, Subscribe.BASIC_PLAYER);
            memberRepository.save(player);
            JoinCompetitionDto joinCompetitionDto = JoinCompetitionDto.builder()
                    .competitionId(recruitingCompetition.getCompetitionId())
                    .uid(player.getUid())
                    .type(joinType.PLAYER)
                    .formId("111")
                    .build();
            joinCompetitionService.join(joinCompetitionDto);
            // when
            joinCompetitionService.deleteJoinCompetition(joinCompetitionDto);

            // then
            Assertions.assertEquals(Optional.empty(), joinCompetitionRepository.findByIdUidAndIdCompetitionId(recruitingCompetition.getCompetitionId(), player.getUid()));
        }
        @Test
        @DisplayName("이미 시작된 대회에는 참가 취소가 불가능하다.")
        void 이미_시작된_대회() {
            // given
            Member host = memberRepository.save(getMember(1, userTypeInst, Subscribe.BASIC_HOST));
            Competition recruitingCompetition = competitionRepository.save(getCompetition(host, LocalDateTime.now().minusDays(8), CompetitionState.RECRUITING));
            JoinCompetitionService joinCompetitionService = new JoinCompetitionService(competitionRepository, competitionCustomRepository, memberRepository, joinCompetitionRepository, participantRepository);
            Member player = getMember(2, userTypePlayer, Subscribe.BASIC_PLAYER);
            memberRepository.save(player);
            JoinCompetitionDto joinCompetitionDto = JoinCompetitionDto.builder()
                    .competitionId(recruitingCompetition.getCompetitionId())
                    .uid(player.getUid())
                    .type(joinType.PLAYER)
                    .formId("111")
                    .build();
            joinCompetitionService.join(joinCompetitionDto);
            // when
            recruitingCompetition.setState(CompetitionState.IN_PROGRESS);

            // then
            Assertions.assertThrows(IllegalArgumentException.class, () -> joinCompetitionService.deleteJoinCompetition(joinCompetitionDto));
        }
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
