package PlayMakers.SportsIT.competition;

import PlayMakers.SportsIT.annotation.MainCompetitionPolicy;
import PlayMakers.SportsIT.domain.*;
import PlayMakers.SportsIT.dto.CompetitionDto;
import PlayMakers.SportsIT.exceptions.competition.IllegalMemberTypeException;
import PlayMakers.SportsIT.repository.CompetitionRepository;
import PlayMakers.SportsIT.repository.MemberRepository;
import PlayMakers.SportsIT.service.CompetitionService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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
class CompetitionServiceUnitTest {
    @Mock
    CompetitionRepository competitionRepository;
    @Mock
    MemberRepository memberRepository;
    @Mock @MainCompetitionPolicy
    CompetitionPolicy competitionPolicy;
    @InjectMocks
    CompetitionService competitionService;

    MemberType userTypePlayer = MemberType.builder()
            .roleName("ROLE_PLAYER")
            .build();
    MemberType userTypeInst = MemberType.builder()
            .roleName("ROLE_INSTITUTION")
            .build();
    MemberType userTypeAdmin = MemberType.builder()
            .roleName("ROLE_ADMIN")
            .build();

    @Nested
    @DisplayName("대회 생성 권한 테스트")
    class 대회생성권한테스트{
        @Test
        @DisplayName("주최자는 대회를 생성할 수 있다.")
        public void 주최자대회생성() {

            // given 호스트가 대회 생성
            Long memberId = 1L;
            Member host = Member.builder().uid(memberId).memberType(Collections.singleton(userTypeInst)).build();
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
            Competition mockCompetition = dto.toEntity();

            // when 대회 dto 생성
            given(memberRepository.findById(memberId)).willReturn(Optional.ofNullable(host));
            given(competitionRepository.save(any(Competition.class))).willReturn(Optional.of(mockCompetition).get());
            Competition created = competitionService.create(dto);

            // then
            assertNotNull(created);
        }

        @Test
        @DisplayName("체육인은 대회를 생성할 수 없다.")
        public void 체육인대회생성실패() {
            //given
            Long memberId = 1L;
            Member host = Member.builder().uid(memberId).memberType(Collections.singleton(userTypePlayer)).build();

            //when
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

            given(memberRepository.findById(memberId)).willReturn(Optional.ofNullable(host));

            //then
            try {
                assertThrows(IllegalMemberTypeException.class, () -> competitionService.create(dto));
            } catch (IllegalMemberTypeException e) {
                log.info(e.getMessage());
                assertEquals("대회 생성 권한이 없습니다.", e.getMessage());
            }
        }
    }

    @Test
    @DisplayName("대회 필수 정보가 누락되면 대회를 생성할 수 없다.")
    public void 대회생성실패_필수정보누락() {
        //given
        Long memberId = 1L;
        Member host = Member.builder().uid(memberId).memberType(Collections.singleton(userTypeInst)).build();

        //when
        CompetitionDto dto = CompetitionDto.builder()
                .host(host)
                .sportCategory(SportCategory.ARM_WRESTLING)
                .totalPrize(10000)
                .location("대회장소")
                .locationDetail("대회장소상세")
                .state(CompetitionState.RECRUITING)
                .startDate(LocalDateTime.parse("2021-03-01T00:00:00"))
                .recruitingStart(LocalDateTime.parse("2021-01-31T00:00:00"))
                .recruitingEnd(LocalDateTime.parse("2021-02-28T00:00:00"))
                .competitionType(CompetitionType.FREE)
                .build();

        given(memberRepository.findById(memberId)).willReturn(Optional.ofNullable(host));

        //then
        try {
            Competition created = competitionService.create(dto);
        } catch (IllegalArgumentException e) {
            assertEquals("필수 정보가 없습니다.\n대회 이름\n대회 내용", e.getMessage());
        }
        assertThrows(IllegalArgumentException.class, () -> competitionService.create(dto));
    }

    @Nested
    @DisplayName("대회 모집일 검증")
    class 모집일검증 {
        @Test
        @DisplayName("모집시작일이 모집종료일보다 늦으면 대회를 생성할 수 없다.")
        public void 모집일검증1(){
            //given
            Long memberId = 1L;
            Member host = Member.builder().uid(memberId).memberType(Collections.singleton(userTypeInst)).build();

            //when
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
                    .recruitingStart(LocalDateTime.parse("2021-02-28T00:00:00"))
                    .recruitingEnd(LocalDateTime.parse("2021-01-31T00:00:00"))
                    .competitionType(CompetitionType.FREE)
                    .build();

            given(memberRepository.findById(memberId)).willReturn(Optional.ofNullable(host));

            //then
            assertFalse(dto.getRecruitingEnd().isAfter(dto.getRecruitingStart()));
            assertThrows(IllegalArgumentException.class, () -> competitionService.create(dto));
        }

        @Test
        @DisplayName("모집마감일이 대회시작일보다 늦으면 대회를 생성할 수 없다.")
        public void 모집일검증2(){
            //given
            Long memberId = 1L;
            Member host = Member.builder().uid(memberId).memberType(Collections.singleton(userTypeInst)).build();

            //when
            CompetitionDto dto = CompetitionDto.builder()
                    .name("대회이름")
                    .host(host)
                    .sportCategory(SportCategory.ARM_WRESTLING)
                    .totalPrize(10000)
                    .content("대회내용")
                    .location("대회장소")
                    .locationDetail("대회장소상세")
                    .state(CompetitionState.RECRUITING)
                    .startDate(LocalDateTime.parse("2021-01-31T00:00:00"))
                    .recruitingStart(LocalDateTime.parse("2021-01-31T00:00:00"))
                    .recruitingEnd(LocalDateTime.parse("2021-02-28T00:00:00"))
                    .competitionType(CompetitionType.FREE)
                    .build();

            given(memberRepository.findById(memberId)).willReturn(Optional.ofNullable(host));

            //then
            assertFalse(dto.getStartDate().isAfter(dto.getRecruitingEnd()));
            assertThrows(IllegalArgumentException.class, () -> competitionService.create(dto));
        }
    }

    @Nested
    @DisplayName("대회 상태 검증 테스트")
    class 대회상태검증 {

        @Test
        @DisplayName("오늘 날짜가 대회 모집시작일보다 빠르면 대회 상태는 모집전이다.")
        public void 모집전_대회생성() {
            // given
            Member host = Member.builder().uid(1L).memberType(Collections.singleton(userTypeInst)).build();
            LocalDateTime today = LocalDateTime.now();
            LocalDateTime recruitingStart = today.plusDays(1);
            LocalDateTime recruitingEnd = today.plusDays(2);
            LocalDateTime startDate = today.plusDays(3);

            // when
            CompetitionDto dto = getCompetitionDto(host, recruitingStart, recruitingEnd, startDate);
            Competition mockCompetition = dto.toEntity();

            given(memberRepository.findById(1L)).willReturn(Optional.ofNullable(host));
            given(competitionRepository.save(any(Competition.class))).willReturn(mockCompetition);
            given(competitionPolicy.getCompetitionState(any(Competition.class))).willReturn(CompetitionState.PLANNING);

            mockCompetition.setState(competitionPolicy.getCompetitionState(mockCompetition));

            // then
            Competition created = competitionService.create(dto);
            assertEquals(CompetitionState.PLANNING, created.getState());

        }

        @Test
        @DisplayName("오늘 날짜가 대회 모집시작일과 모집종료일 사이면 대회 상태는 모집중이다.")
        public void 모집중_대회생성() {
            // given
            Member host = Member.builder().uid(1L).memberType(Collections.singleton(userTypeInst)).build();
            LocalDateTime today = LocalDateTime.now();
            LocalDateTime recruitingStart = today.plusDays(-1);
            LocalDateTime recruitingEnd = today.plusDays(2);
            LocalDateTime startDate = today.plusDays(3);

            // when
            CompetitionDto dto = getCompetitionDto(host, recruitingStart, recruitingEnd, startDate);
            Competition mockCompetition = dto.toEntity();

            given(memberRepository.findById(1L)).willReturn(Optional.ofNullable(host));
            given(competitionRepository.save(any(Competition.class))).willReturn(mockCompetition);
            given(competitionPolicy.getCompetitionState(any(Competition.class))).willReturn(CompetitionState.RECRUITING);

            mockCompetition.setState(competitionPolicy.getCompetitionState(mockCompetition));

            // then
            Competition created = competitionService.create(dto);
            assertEquals(CompetitionState.RECRUITING, created.getState());
        }

        @Test
        @DisplayName("오늘 날짜가 대회 모집종료일과 대회시작일 사이면 대회 상태는 모집마감이다.")
        public void 모집마감_대회생성() {
            // given
            Member host = Member.builder().uid(1L).memberType(Collections.singleton(userTypeInst)).build();
            LocalDateTime today = LocalDateTime.now();
            LocalDateTime recruitingStart = today.plusDays(-2);
            LocalDateTime recruitingEnd = today.plusDays(-1);
            LocalDateTime startDate = today.plusDays(3);

            // when
            CompetitionDto dto = getCompetitionDto(host, recruitingStart, recruitingEnd, startDate);
            Competition mockCompetition = dto.toEntity();

            given(memberRepository.findById(1L)).willReturn(Optional.ofNullable(host));
            given(competitionRepository.save(any(Competition.class))).willReturn(mockCompetition);
            given(competitionPolicy.getCompetitionState(any(Competition.class))).willReturn(CompetitionState.RECRUITING_END);

            mockCompetition.setState(competitionPolicy.getCompetitionState(mockCompetition));

            // then
            Competition created = competitionService.create(dto);
            assertEquals(CompetitionState.RECRUITING_END, created.getState());
        }

        @Test
        @DisplayName("오늘 날짜가 대회 대회시작일 이후면 대회 상태는 대회중이다.")
        public void 대회중_대회생성() {
            // given
            Member host = Member.builder().uid(1L).memberType(Collections.singleton(userTypeInst)).build();
            LocalDateTime today = LocalDateTime.now();
            LocalDateTime recruitingStart = today.plusDays(-3);
            LocalDateTime recruitingEnd = today.plusDays(-2);
            LocalDateTime startDate = today.plusDays(-1);

            // when
            CompetitionDto dto = getCompetitionDto(host, recruitingStart, recruitingEnd, startDate);
            Competition mockCompetition = dto.toEntity();

            given(memberRepository.findById(1L)).willReturn(Optional.ofNullable(host));
            given(competitionRepository.save(any(Competition.class))).willReturn(mockCompetition);
            given(competitionPolicy.getCompetitionState(any(Competition.class))).willReturn(CompetitionState.IN_PROGRESS);

            mockCompetition.setState(competitionPolicy.getCompetitionState(mockCompetition));

            // then
            Competition created = competitionService.create(dto);
            //CompetitionService.autoSetCompetitionState(created);
            assertEquals(CompetitionState.IN_PROGRESS, created.getState());
        }

        private CompetitionDto getCompetitionDto(Member host, LocalDateTime recruitingStart, LocalDateTime recruitingEnd, LocalDateTime startDate) {
            CompetitionDto dto = CompetitionDto.builder().name("대회이름").host(host).sportCategory(SportCategory.ARM_WRESTLING).totalPrize(10000)
                    .content("대회내용").location("대회장소").locationDetail("대회장소상세").startDate(startDate) // dto.state 없이 빌드
                    .recruitingStart(recruitingStart).recruitingEnd(recruitingEnd).competitionType(CompetitionType.FREE).build();
            return dto;
        }

    }

    @Nested
    @DisplayName("대회 상품 타입 검증 테스트")
    class 대회타입검증 {
        @Test
        @DisplayName("주최자 타입이 HOST_BASIC이면 대회 타입은 FREE이다.")
        public void FREE타입_대회생성() {
            // given
            Member host = Member.builder().uid(1L).memberType(Collections.singleton(userTypeInst)).build();
            CompetitionDto dto = getCompetitionDto(host);
            Competition mockCompetition = dto.toEntity();

            given(memberRepository.findById(1L)).willReturn(Optional.ofNullable(host));
            given(competitionPolicy.getCompetitionType(host)).willReturn(CompetitionType.FREE);
            given(competitionRepository.save(any(Competition.class))).willReturn(mockCompetition);

            mockCompetition.setCompetitionType(competitionPolicy.getCompetitionType(host));

            // then
            Competition created = competitionService.create(dto);
            assertEquals(CompetitionType.FREE, created.getCompetitionType());
        }
        @Test
        @DisplayName("주최자 타입이 HOST_BASIC이면 대회 타입은 FREE이다.")
        public void PREMIUM타입_대회생성() {
            // given and when
            Member host = Member.builder().uid(1L).memberType(Collections.singleton(userTypeInst)).build();
            CompetitionDto dto = getCompetitionDto(host);
            Competition mockCompetition = dto.toEntity();

            given(memberRepository.findById(1L)).willReturn(Optional.ofNullable(host));
            given(competitionPolicy.getCompetitionType(host)).willReturn(CompetitionType.PREMIUM);
            given(competitionRepository.save(any(Competition.class))).willReturn(mockCompetition);

            mockCompetition.setCompetitionType(competitionPolicy.getCompetitionType(host));

            // then
            Competition created = competitionService.create(dto);
            assertEquals(CompetitionType.PREMIUM, created.getCompetitionType());
        }
        @Test
        @DisplayName("주최자 타입이 HOST_BASIC이면 대회 타입은 FREE이다.")
        public void VIP타입_대회생성() {
            // given and when
            Member host = Member.builder().uid(1L).memberType(Collections.singleton(userTypeInst)).build();
            CompetitionDto dto = getCompetitionDto(host);
            Competition mockCompetition = dto.toEntity();

            given(memberRepository.findById(1L)).willReturn(Optional.ofNullable(host));
            given(competitionPolicy.getCompetitionType(host)).willReturn(CompetitionType.VIP);
            given(competitionRepository.save(any(Competition.class))).willReturn(mockCompetition);

            mockCompetition.setCompetitionType(competitionPolicy.getCompetitionType(host));

            // then
            Competition created = competitionService.create(dto);
            log.info("created : {}", created);
            assertEquals(CompetitionType.VIP, created.getCompetitionType());
        }

        private CompetitionDto getCompetitionDto(Member host) {
            CompetitionDto dto = CompetitionDto.builder()
                    .name("대회이름")
                    .host(host)
                    .sportCategory(SportCategory.ARM_WRESTLING)
                    .totalPrize(10000)
                    .content("대회내용")
                    .location("대회장소")
                    .locationDetail("대회장소상세")
                    .startDate(LocalDateTime.parse("2023-03-15T00:00:00"))
                    .recruitingStart(LocalDateTime.parse("2023-01-31T00:00:00"))
                    .recruitingEnd(LocalDateTime.parse("2023-02-28T00:00:00"))
                    .build();
            return dto;
        }
    }
    /*
    대회 조회 점검 사항
    1. 대회 조회 시 대회 상태에 따라 조회되는 대회가 다르다. (PLANNING, RECRUITING, RECRUITING_END, IN_PROGRESS, END, SUSPEND)
    2. 대회 조회 시 대회 종목으로 조회할 수 있다.
    3. 대회 조회 시 대회 이름으로 조회할 수 있다.
    4. 대회 조회 시 대회 주최자로 조회할 수 있다.
    5. 대회 조회 시 대회
     */

    /*
    대회 수정 점검 사항
    1. 대회 시작일 이후에는 대회 정보를 수정할 수 없다.

     */
}