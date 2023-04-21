package PlayMakers.SportsIT.competition;

import PlayMakers.SportsIT.domain.*;
import PlayMakers.SportsIT.dto.CompetitionDto;
import PlayMakers.SportsIT.repository.CompetitionRepository;
import PlayMakers.SportsIT.repository.MemberRepository;
import PlayMakers.SportsIT.service.CompetitionService;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.will;
import static org.mockito.Mockito.when;

@Slf4j
@ExtendWith(MockitoExtension.class)
class CompetitionServiceUnitTest {
    @Mock
    CompetitionRepository competitionRepository;
    @Mock
    MemberRepository memberRepository;

    @InjectMocks
    CompetitionService competitionService;

    MemberType userTypePlayer = MemberType.builder()
            .roleName("ROLE_INSTITUTION")
            .build();
    MemberType userTypeInst = MemberType.builder()
            .roleName("ROLE_INSTITUTION")
            .build();
    MemberType userTypeAdmin = MemberType.builder()
            .roleName("ROLE_INSTITUTION")
            .build();

    @Test
    @DisplayName("주최자는 대회를 생성할 수 있다.")
    public void 주최자대회생성() {

        // given 호스트가 대회 생성
        Long memberId = 1L;
        //Member host = memberRepository.findByEmail("host@gmail.com");
        Member host = Member.builder().uid(memberId).memberType(Collections.singleton(userTypeInst)).build();

        // host가 널일 경우
        CompetitionDto dto = CompetitionDto.builder()
                .name("대회이름")
                .host(host)
                .sportCategory(SportCategory.ARM_WRESTLING)
                .totalPrize(10000)
                .content("대회내용")
                .location("대회장소")
                .locationDetail("대회장소상세")
                .state(CompetitionState.RECRUITING)
                .startDate(LocalDateTime.parse("2021-03-01T00:00:00"))
                .recruitingStart(LocalDateTime.parse("2021-01-31T00:00:00"))
                .recruitingEnd(LocalDateTime.parse("2021-02-28T00:00:00"))
                .competitionType(CompetitionType.FREE)
                .build();
        Competition mockCompetition = Competition.builder()
                .competitionId(1L)
                .name("대회이름")
                .host(host)
                .category(SportCategory.ARM_WRESTLING)
                .totalPrize(10000)
                .content("대회내용")
                .location("대회장소")
                .locationDetail("대회장소상세")
                .state(CompetitionState.RECRUITING)
                .startDate(LocalDateTime.parse("2021-03-01T00:00:00"))
                .recruitingStart(LocalDateTime.parse("2021-01-31T00:00:00"))
                .recruitingEnd(LocalDateTime.parse("2021-02-28T00:00:00"))
                .competitionType(CompetitionType.FREE)
                .build();

        // when 대회 dto 생성
        given(memberRepository.findById(memberId)).willReturn(Optional.ofNullable(host));
        given(competitionRepository.save(any(Competition.class))).willReturn(Optional.of(mockCompetition).get());
        Competition created = competitionService.create(dto);

        // then
        assertNotNull(created);
    }
}