package PlayMakers.SportsIT.competition;

import PlayMakers.SportsIT.annotation.MainCompetitionPolicy;
import PlayMakers.SportsIT.domain.*;
import PlayMakers.SportsIT.dto.JoinCompetitionDto;
import PlayMakers.SportsIT.repository.CompetitionRepository;
import PlayMakers.SportsIT.repository.JoinCompetitionRepository;
import PlayMakers.SportsIT.repository.MemberRepository;
import PlayMakers.SportsIT.service.JoinCompetitionService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@Slf4j
@ExtendWith(MockitoExtension.class)
class JoinCompetitionServiceUnitTest {
    @Mock
    private CompetitionRepository competitionRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock @MainCompetitionPolicy
    CompetitionPolicy competitionPolicy;
    @Mock
    private JoinCompetitionRepository joinCompetitionRepository;
    @InjectMocks
    private JoinCompetitionService joinCompetitionService;

    MemberType userTypePlayer = MemberType.builder()
            .roleName("ROLE_PLAYER")
            .build();
    MemberType userTypeInst = MemberType.builder()
            .roleName("ROLE_INSTITUTION")
            .build();

    @BeforeEach
    void setUp() {
    }

    private static Competition createCompetition(Member host) {
        Competition competition = Competition.builder()
                .competitionId(1L)
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
        return competition;
    }

    private Member createHost() {
        Member host = Member.builder().uid(1L).memberType(Collections.singleton(userTypeInst)).build();
        return host;
    }

    @Test
    @DisplayName("체육인은 대회에 참가할 수 있다.")
    void joinCompetition() {
        // given
        Member host = createHost();
        Competition newCompetition = createCompetition(host);
        log.info("newCompetition: {}", newCompetition);

        Member player = Member.builder().uid(2L).memberType(Collections.singleton(userTypePlayer)).build();

        JoinCompetitionDto joinCompetitionDto = JoinCompetitionDto.builder()
                .uid(player.getUid())
                .competitionId(newCompetition.getCompetitionId())
                .type(JoinCompetition.joinType.PLAYER)
                .formId("1")
                .build();
        log.info("joinCompetitionDto: {}", joinCompetitionDto);

        //when
        JoinCompetition mockJoin = joinCompetitionDto.toEntity();
        mockJoin.setMember(player);
        mockJoin.setCompetition(newCompetition);
        log.info("mockJoin: {}", mockJoin);

        given(memberRepository.findById(any(Long.class))).willReturn(Optional.of(player));
        given(competitionRepository.findById(any(Long.class))).willReturn(Optional.of(newCompetition));
        given(joinCompetitionRepository.save(any(JoinCompetition.class))).willReturn(mockJoin);

        JoinCompetition newJoin = joinCompetitionService.join(joinCompetitionDto);
        log.info("newJoin: {}", newJoin);


        //then
        assertNotNull(newJoin);
        assertEquals(newJoin, mockJoin);
    }

    @Test
    @DisplayName("체육인은 대회 참가 정보(formId)를 수정할 수 있다.")
    void updateJoinCompetition() {
        // given
        Member host = createHost();
        Competition newCompetition = createCompetition(host);

        Member player = Member.builder().uid(2L).memberType(Collections.singleton(userTypePlayer)).build();

        JoinCompetitionDto joinCompetitionDto = JoinCompetitionDto.builder()
                .uid(player.getUid())
                .competitionId(newCompetition.getCompetitionId())
                .type(JoinCompetition.joinType.PLAYER)
                .formId("1")
                .build();

        JoinCompetition mockJoin = joinCompetitionDto.toEntity();
        mockJoin.setMember(player);
        mockJoin.setCompetition(newCompetition);
        log.info("mockJoin: {}", mockJoin);

        given(memberRepository.findById(any(Long.class))).willReturn(Optional.of(player));
        given(competitionRepository.findById(any(Long.class))).willReturn(Optional.of(newCompetition));
        given(joinCompetitionRepository.save(any(JoinCompetition.class))).willReturn(mockJoin);

        JoinCompetition newJoin = joinCompetitionService.join(joinCompetitionDto);
        log.info("newJoin: {}", newJoin);

        //when
        JoinCompetitionDto updateJoinCompetitionDto = JoinCompetitionDto.builder()
                .uid(player.getUid())
                .competitionId(newCompetition.getCompetitionId())
                .type(JoinCompetition.joinType.PLAYER)
                .formId("2")
                .build();

        JoinCompetition updateJoinCompetition = updateJoinCompetitionDto.toEntity();
        log.info("updateJoinCompetition: {}", updateJoinCompetition);

        given(joinCompetitionRepository.findByIdUidAndIdCompetitionId(player.getUid(), newCompetition.getCompetitionId())).willReturn(Optional.of(mockJoin));
        given(joinCompetitionRepository.save(any(JoinCompetition.class))).willReturn(updateJoinCompetition);

        JoinCompetition updatedJoinCompetition = joinCompetitionService.updateJoinCompetition(updateJoinCompetitionDto);

        //then
        assertEquals(updatedJoinCompetition, updateJoinCompetition);

    }
}