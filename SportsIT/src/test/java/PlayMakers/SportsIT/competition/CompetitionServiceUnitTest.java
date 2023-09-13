package PlayMakers.SportsIT.competition;

import PlayMakers.SportsIT.annotation.MainCompetitionPolicy;
import PlayMakers.SportsIT.domain.*;
import PlayMakers.SportsIT.dto.CompetitionDto;
import PlayMakers.SportsIT.enums.CompetitionType;
import PlayMakers.SportsIT.exceptions.InvalidValueException;
import PlayMakers.SportsIT.exceptions.UnAuthorizedException;
import PlayMakers.SportsIT.exceptions.competition.IllegalMemberTypeException;
import PlayMakers.SportsIT.repository.CategoryRepository;
import PlayMakers.SportsIT.repository.CompetitionRepository;
import PlayMakers.SportsIT.repository.MemberRepository;
import PlayMakers.SportsIT.service.CompetitionService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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
    @Mock
    CategoryRepository categoryRepository;
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
                    .endDate(LocalDateTime.parse("2021-03-05T00:00:00"))
                    .recruitingStart(LocalDateTime.parse("2021-01-31T00:00:00"))
                    .recruitingEnd(LocalDateTime.parse("2021-02-28T00:00:00"))
                    .competitionType(CompetitionType.FREE)
                    .build();
            Competition mockCompetition = dto.toEntity();

            // when 대회 dto 생성
            given(competitionRepository.save(any(Competition.class))).willReturn(Optional.of(mockCompetition).get());
            given(categoryRepository.findById(any())).willReturn(Optional.ofNullable(Category.builder().category("ETC").name("기타").build()));
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


            //then
            assertThrows(UnAuthorizedException.class, () -> competitionService.create(dto));

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
                .categories(new ArrayList<>(Arrays.asList("ARM_WRESTLING")))
                .totalPrize(10000)
                .location("대회장소")
                .locationDetail("대회장소상세")
                .state(CompetitionState.RECRUITING)
                .startDate(LocalDateTime.parse("2021-03-01T00:00:00"))
                .endDate(LocalDateTime.parse("2021-03-05T00:00:00"))
                .recruitingStart(LocalDateTime.parse("2021-01-31T00:00:00"))
                .recruitingEnd(LocalDateTime.parse("2021-02-28T00:00:00"))
                .competitionType(CompetitionType.FREE)
                .build();

        given(categoryRepository.findById(any())).willReturn(Optional.ofNullable(Category.builder().category("ETC").name("기타").build()));

        //then
        assertThrows(InvalidValueException.class, () -> competitionService.create(dto));
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
                    .endDate(LocalDateTime.parse("2021-03-05T00:00:00"))
                    .recruitingStart(LocalDateTime.parse("2021-02-28T00:00:00"))
                    .recruitingEnd(LocalDateTime.parse("2021-01-31T00:00:00"))
                    .competitionType(CompetitionType.FREE)
                    .build();

            given(categoryRepository.findById(any())).willReturn(Optional.ofNullable(Category.builder().category("ETC").name("기타").build()));
            //then
            assertFalse(dto.getRecruitingEnd().isAfter(dto.getRecruitingStart()));
            assertThrows(InvalidValueException.class, () -> competitionService.create(dto));
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
                    .endDate(LocalDateTime.parse("2021-03-05T00:00:00"))
                    .recruitingStart(LocalDateTime.parse("2021-01-31T00:00:00"))
                    .recruitingEnd(LocalDateTime.parse("2021-02-28T00:00:00"))
                    .competitionType(CompetitionType.FREE)
                    .build();

            given(categoryRepository.findById(any())).willReturn(Optional.ofNullable(Category.builder().category("ETC").name("기타").build()));
            //then
            assertFalse(dto.getStartDate().isAfter(dto.getRecruitingEnd()));
            assertThrows(InvalidValueException.class, () -> competitionService.create(dto));
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
            LocalDateTime endDate = today.plusDays(4);

            // when
            CompetitionDto dto = getCompetitionDto(host, recruitingStart, recruitingEnd, startDate, endDate);
            Competition mockCompetition = dto.toEntity();

            given(competitionRepository.save(any(Competition.class))).willReturn(mockCompetition);
            given(competitionPolicy.getCompetitionState(any(Competition.class))).willReturn(CompetitionState.PLANNING);
            given(categoryRepository.findById(any())).willReturn(Optional.ofNullable(Category.builder().category("ETC").name("기타").build()));
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
            LocalDateTime endDate = today.plusDays(4);

            // when
            CompetitionDto dto = getCompetitionDto(host, recruitingStart, recruitingEnd, startDate, endDate);
            Competition mockCompetition = dto.toEntity();

            given(competitionRepository.save(any(Competition.class))).willReturn(mockCompetition);
            given(competitionPolicy.getCompetitionState(any(Competition.class))).willReturn(CompetitionState.RECRUITING);
            given(categoryRepository.findById(any())).willReturn(Optional.ofNullable(Category.builder().category("ETC").name("기타").build()));
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
            LocalDateTime endDate = today.plusDays(4);

            // when
            List<String> categories = new ArrayList<>() {{add("기타");}};
            CompetitionDto dto = getCompetitionDto(host, recruitingStart, recruitingEnd, startDate, endDate);
            Competition mockCompetition = dto.toEntity();

            given(competitionRepository.save(any(Competition.class))).willReturn(mockCompetition);
            given(competitionPolicy.getCompetitionState(any(Competition.class))).willReturn(CompetitionState.RECRUITING_END);
            given(categoryRepository.findById(any())).willReturn(Optional.ofNullable(Category.builder().category("ETC").name("기타").build()));
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
            LocalDateTime endDate = today.plusDays(4);

            // when
            CompetitionDto dto = getCompetitionDto(host, recruitingStart, recruitingEnd, startDate, endDate);
            Competition mockCompetition = dto.toEntity();
            Category category = Category.builder().category("ETC").name("기타").build();

            given(competitionRepository.save(any(Competition.class))).willReturn(mockCompetition);
            given(competitionPolicy.getCompetitionState(any(Competition.class))).willReturn(CompetitionState.IN_PROGRESS);
            given(categoryRepository.findById(any(String.class))).willReturn(Optional.ofNullable(category));

            mockCompetition.setState(competitionPolicy.getCompetitionState(mockCompetition));

            // then
            Competition created = competitionService.create(dto);
            //CompetitionService.autoSetCompetitionState(created);
            assertEquals(CompetitionState.IN_PROGRESS, created.getState());
        }

        private CompetitionDto getCompetitionDto(Member host, LocalDateTime recruitingStart, LocalDateTime recruitingEnd, LocalDateTime startDate, LocalDateTime endDate) {
            CompetitionDto dto = CompetitionDto.builder().name("대회이름").host(host).sportCategory(SportCategory.ARM_WRESTLING).totalPrize(10000)
                    .content("대회내용").location("대회장소").locationDetail("대회장소상세").startDate(startDate).endDate(endDate) // dto.state 없이 빌드
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

            given(competitionPolicy.getCompetitionType(host)).willReturn(CompetitionType.FREE);
            given(competitionRepository.save(any(Competition.class))).willReturn(mockCompetition);
            given(categoryRepository.findById(any())).willReturn(Optional.ofNullable(Category.builder().category("ETC").name("기타").build()));
            mockCompetition.setCompetitionType(competitionPolicy.getCompetitionType(host));

            // then
            Competition created = competitionService.create(dto);
            assertEquals(CompetitionType.FREE, created.getCompetitionType());
        }
        @Test
        @DisplayName("주최자 타입이 HOST_PREMIUM이면 대회 타입은 PREMIUM이다.")
        public void PREMIUM타입_대회생성() {
            // given and when
            Member host = Member.builder().uid(1L).memberType(Collections.singleton(userTypeInst)).build();
            CompetitionDto dto = getCompetitionDto(host);
            Competition mockCompetition = dto.toEntity();

            given(competitionPolicy.getCompetitionType(host)).willReturn(CompetitionType.PREMIUM);
            given(competitionRepository.save(any(Competition.class))).willReturn(mockCompetition);
            given(categoryRepository.findById(any())).willReturn(Optional.ofNullable(Category.builder().category("ETC").name("기타").build()));
            mockCompetition.setCompetitionType(competitionPolicy.getCompetitionType(host));

            // then
            Competition created = competitionService.create(dto);
            assertEquals(CompetitionType.PREMIUM, created.getCompetitionType());
        }
        @Test
        @DisplayName("주최자 타입이 HOST_VIP이면 대회 타입은 VIP이다.")
        public void VIP타입_대회생성() {
            // given and when
            Member host = Member.builder().uid(1L).memberType(Collections.singleton(userTypeInst)).build();
            CompetitionDto dto = getCompetitionDto(host);
            Competition mockCompetition = dto.toEntity();

            given(competitionPolicy.getCompetitionType(host)).willReturn(CompetitionType.VIP);
            given(competitionRepository.save(any(Competition.class))).willReturn(mockCompetition);
            given(categoryRepository.findById(any())).willReturn(Optional.ofNullable(Category.builder().category("ETC").name("기타").build()));
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
                    .endDate(LocalDateTime.parse("2023-03-20T00:00:00"))
                    .recruitingStart(LocalDateTime.parse("2023-01-31T00:00:00"))
                    .recruitingEnd(LocalDateTime.parse("2023-02-28T00:00:00"))
                    .build();
            return dto;
        }
    }
    @Nested
    @DisplayName("대회 Slice 검색 테스트")
    class 대회Slice검색 {
        List<Member> members = new ArrayList<>();
        List<Competition> competitions = new ArrayList<>();

        @BeforeEach
        public void init() {
            LocalDateTime today = LocalDateTime.parse("2023-03-20T00:00:00");

            members.add(Member.builder().uid(1L).name("홍길동").memberType(Collections.singleton(userTypeInst)).build());
            members.add(Member.builder().uid(2L).name("공명규").memberType(Collections.singleton(userTypeInst)).build());
            members.add(Member.builder().uid(3L).name("김영훈").memberType(Collections.singleton(userTypeInst)).build());
            members.add(Member.builder().uid(4L).name("신우현").memberType(Collections.singleton(userTypeInst)).build());
            members.add(Member.builder().uid(5L).name("이준수").memberType(Collections.singleton(userTypeInst)).build());

            competitions.add(Competition.builder().competitionId(1L).name("볼랜드 20회 정기대회").host(members.get(0)).category(SportCategory.SOCCER).totalPrize(10000).state(CompetitionState.END).startDate(LocalDateTime.parse("2023-03-15T00:00:00")).recruitingStart(LocalDateTime.parse("2023-01-31T00:00:00")).recruitingEnd(LocalDateTime.parse("2023-02-28T00:00:00")).competitionType(CompetitionType.FREE).viewCount(500).scrapCount(50).build());
            competitions.add(Competition.builder().competitionId(2L).name("볼랜드 21회 정기대회").host(members.get(0)).category(SportCategory.SOCCER).totalPrize(10000).state(CompetitionState.RECRUITING).startDate(LocalDateTime.parse("2023-03-30T00:00:00")).recruitingStart(LocalDateTime.parse("2023-03-20T00:00:00")).recruitingEnd(LocalDateTime.parse("2023-03-25T00:00:00")).competitionType(CompetitionType.FREE).viewCount(400).scrapCount(45).build());
            competitions.add(Competition.builder().competitionId(3L).name("볼랜드 22회 정기대회").host(members.get(0)).category(SportCategory.SOCCER).totalPrize(10000).state(CompetitionState.PLANNING).startDate(LocalDateTime.parse("2023-05-15T00:00:00")).recruitingStart(LocalDateTime.parse("2023-04-25T00:00:00")).recruitingEnd(LocalDateTime.parse("2023-04-30T00:00:00")).competitionType(CompetitionType.FREE).viewCount(300).scrapCount(40).build());
            competitions.add(Competition.builder().competitionId(4L).name("홍길동배 탁구 대회").host(members.get(1)).category(SportCategory.TABLE_TENNIS).totalPrize(100000).state(CompetitionState.RECRUITING).startDate(LocalDateTime.parse("2023-03-30T00:00:00")).recruitingStart(LocalDateTime.parse("2023-03-15T00:00:00")).recruitingEnd(LocalDateTime.parse("2023-03-25T00:00:00")).competitionType(CompetitionType.FREE).viewCount(300).scrapCount(35).build());
            competitions.add(Competition.builder().competitionId(5L).name("수원시 탁구 대회").host(members.get(1)).category(SportCategory.TABLE_TENNIS).totalPrize(200000).state(CompetitionState.RECRUITING).startDate(LocalDateTime.parse("2023-05-15T00:00:00")).recruitingStart(LocalDateTime.parse("2023-03-10T00:00:00")).recruitingEnd(LocalDateTime.parse("2023-04-30T00:00:00")).competitionType(CompetitionType.PREMIUM).viewCount(250).scrapCount(35).build());
            competitions.add(Competition.builder().competitionId(6L).name("영통구 탁구 대전").host(members.get(1)).category(SportCategory.TABLE_TENNIS).totalPrize(0).state(CompetitionState.PLANNING).startDate(LocalDateTime.parse("2023-05-15T00:00:00")).recruitingStart(LocalDateTime.parse("2023-04-30T00:00:00")).recruitingEnd(LocalDateTime.parse("2023-05-05T00:00:00")).competitionType(CompetitionType.FREE).viewCount(230).scrapCount(20).build());
            competitions.add(Competition.builder().competitionId(7L).name("3월 대전시 한마음 배드민턴 대회").host(members.get(2)).category(SportCategory.BADMINTON).totalPrize(400000).state(CompetitionState.RECRUITING_END).startDate(LocalDateTime.parse("2023-03-28T00:00:00")).recruitingStart(LocalDateTime.parse("2023-02-28T00:00:00")).recruitingEnd(LocalDateTime.parse("2023-03-15T00:00:00")).competitionType(CompetitionType.PREMIUM).viewCount(200).scrapCount(15).build());
            competitions.add(Competition.builder().competitionId(8L).name("4월 대전시 한마음 배드민턴 대회").host(members.get(2)).category(SportCategory.BADMINTON).totalPrize(400000).state(CompetitionState.RECRUITING).startDate(LocalDateTime.parse("2023-04-05T00:00:00")).recruitingStart(LocalDateTime.parse("2023-03-02T00:00:00")).recruitingEnd(LocalDateTime.parse("2023-03-25T00:00:00")).competitionType(CompetitionType.FREE).viewCount(150).scrapCount(5).build());
            competitions.add(Competition.builder().competitionId(9L).name("5월 대전시 한마음 배드민턴 대회").host(members.get(2)).category(SportCategory.BADMINTON).totalPrize(400000).state(CompetitionState.PLANNING).startDate(LocalDateTime.parse("2023-05-25T00:00:00")).recruitingStart(LocalDateTime.parse("2023-04-05T00:00:00")).recruitingEnd(LocalDateTime.parse("2023-04-25T00:00:00")).competitionType(CompetitionType.PREMIUM).viewCount(100).scrapCount(6).build());
            competitions.add(Competition.builder().competitionId(10L).name("제3회 전국체전 팔씨름 대회").host(members.get(3)).category(SportCategory.ARM_WRESTLING).totalPrize(50000).state(CompetitionState.CANCEL).startDate(LocalDateTime.parse("2021-02-15T00:00:00")).recruitingStart(LocalDateTime.parse("2021-01-03T00:00:00")).recruitingEnd(LocalDateTime.parse("2021-01-20T00:00:00")).competitionType(CompetitionType.FREE).viewCount(50).scrapCount(6).build());
            competitions.add(Competition.builder().competitionId(11L).name("제4회 전국체전 팔씨름 대회").host(members.get(3)).category(SportCategory.ARM_WRESTLING).totalPrize(50000).state(CompetitionState.END).startDate(LocalDateTime.parse("2023-03-15T00:00:00")).recruitingStart(LocalDateTime.parse("2023-01-31T00:00:00")).recruitingEnd(LocalDateTime.parse("2023-02-28T00:00:00")).competitionType(CompetitionType.PREMIUM).viewCount(30).scrapCount(6).build());
            competitions.add(Competition.builder().competitionId(12L).name("제5회 전국체전 팔씨름 대회").host(members.get(3)).category(SportCategory.ARM_WRESTLING).totalPrize(50000).state(CompetitionState.IN_PROGRESS).startDate(LocalDateTime.parse("2023-03-20T00:00:00")).recruitingStart(LocalDateTime.parse("2023-01-31T00:00:00")).recruitingEnd(LocalDateTime.parse("2023-02-28T00:00:00")).competitionType(CompetitionType.FREE).viewCount(30).scrapCount(7).build());
            competitions.add(Competition.builder().competitionId(13L).name("아주대 정기 축구 경기").host(members.get(4)).category(SportCategory.SOCCER).totalPrize(0).state(CompetitionState.RECRUITING).startDate(LocalDateTime.parse("2023-03-30T00:00:00")).recruitingStart(LocalDateTime.parse("2023-02-28T00:00:00")).recruitingEnd(LocalDateTime.parse("2023-03-30T00:00:00")).competitionType(CompetitionType.FREE).viewCount(15).scrapCount(10).build());
            competitions.add(Competition.builder().competitionId(14L).name("아주대 정기 축구 경기").host(members.get(4)).category(SportCategory.SOCCER).totalPrize(0).state(CompetitionState.PLANNING).startDate(LocalDateTime.parse("2023-05-15T00:00:00")).recruitingStart(LocalDateTime.parse("2023-04-30T00:00:00")).recruitingEnd(LocalDateTime.parse("2023-05-28T00:00:00")).competitionType(CompetitionType.FREE).viewCount(0).scrapCount(30).build());
            competitions.add(Competition.builder().competitionId(15L).name("아주대 정기 축구 경기").host(members.get(4)).category(SportCategory.SOCCER).totalPrize(0).state(CompetitionState.PLANNING).startDate(LocalDateTime.parse("2023-06-15T00:00:00")).recruitingStart(LocalDateTime.parse("2023-05-31T00:00:00")).recruitingEnd(LocalDateTime.parse("2023-06-28T00:00:00")).competitionType(CompetitionType.FREE).viewCount(0).scrapCount(25).build());
        }
        @Test
        @DisplayName("대회 Slice 조회 테스트")
        public void 대회Slice조회() {
            // given
            String keyword = null;
            Pageable pageable;

            Pageable pageableCreatedDesc = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdDate"));
            Slice<Competition> expectedSliceSortedByCreatedAt = new SliceImpl<>(competitions.stream().sorted(Comparator.comparing(Competition::getCompetitionId).reversed()).collect(Collectors.toList()).subList(0, 10), pageableCreatedDesc, true);
            Pageable pageableViewCntDesc = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "viewCount"));
            Slice<Competition> expectedSliceSortedByViewCount = new SliceImpl<>(competitions.stream().sorted(Comparator.comparing(Competition::getViewCount).reversed()).sorted(Comparator.comparing(Competition::getCompetitionId).reversed()).collect(Collectors.toList()).subList(0, 10), pageableViewCntDesc, true);
            Pageable pageableScrapCntDesc = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "scrapCount"));
            Slice<Competition> expectedSliceSortedByScrapCount = new SliceImpl<>(competitions.stream().sorted(Comparator.comparing(Competition::getScrapCount).reversed()).sorted(Comparator.comparing(Competition::getCompetitionId).reversed()).collect(Collectors.toList()).subList(0, 10), pageableScrapCntDesc, true);

            // when
            Mockito.when(competitionRepository.findCompetitionBySlice(keyword, null, pageableCreatedDesc)).thenReturn(expectedSliceSortedByCreatedAt);
            Slice<Competition> actualSliceSortedByCreatedAt = competitionService.getCompetitionSlice(keyword, null, null, 0, 10);

            Mockito.when(competitionRepository.findCompetitionBySlice(keyword, null, pageableViewCntDesc)).thenReturn(expectedSliceSortedByViewCount);
            Slice<Competition> actualSliceSortedByViewCount = competitionService.getCompetitionSlice(keyword, null, "viewCount", 0, 10);

            Mockito.when(competitionRepository.findCompetitionBySlice(keyword, null, pageableScrapCntDesc)).thenReturn(expectedSliceSortedByScrapCount);
            Slice<Competition> actualSliceSortedByScrapCount = competitionService.getCompetitionSlice(keyword, null, "scrapCount", 0 ,10);

            for(int i = 0; i < expectedSliceSortedByViewCount.getContent().size(); i++) {
                log.info("expectedSliceSortedByViewCount = {}", expectedSliceSortedByViewCount.getContent().get(i));
                log.info("actualSliceSortedByViewCount = {}", actualSliceSortedByViewCount.getContent().get(i));
            }
            log.info("expectedSliceSortedByViewCount = {}", expectedSliceSortedByViewCount.getSort());
            log.info("actualSliceSortedByViewCount = {}", actualSliceSortedByViewCount.getSort());
            // then
            assertEquals(expectedSliceSortedByCreatedAt, actualSliceSortedByCreatedAt);
            assertEquals(expectedSliceSortedByViewCount, actualSliceSortedByViewCount);
            assertEquals(expectedSliceSortedByScrapCount, actualSliceSortedByScrapCount);

        }
        @Test
        @DisplayName("빈 Slice일 경우 에러를 던진다.")
        public void 대회_빈_Slice_조회() {
            // given
            String keyword = "경마";
            Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdDate"));
            Slice<Competition> expectedSlice = new SliceImpl<>(new ArrayList<>(), pageable, false);

            // when
            //Mockito.when(competitionRepository.findCompetitionBySlice(keyword, null, pageable)).thenReturn(expectedSlice);

            // then
            //assertThrows(EntityNotFoundException.class, () -> competitionService.getCompetitionSlice(keyword,null, null, 0, 10));
        }

        @Test
        @DisplayName("키워드로 대회 Slice 조회를 할 수 있다.")
        public void 대회_키워드로_Slice_조회() {
            // given
            String keyword = "축";

            Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdDate"));
            Slice<Competition> expectedSlice = new SliceImpl<>(competitions.stream().filter(competition -> competition.getName().contains(keyword)||competition.getHost().getName().contains(keyword)||competition.getCategory().getCategoryName().contains(keyword)).collect(Collectors.toList()), pageable, false);

            // when
            Mockito.when(competitionRepository.findCompetitionBySlice(keyword, null, pageable)).thenReturn(expectedSlice);
            Slice<Competition> actualSlice = competitionService.getCompetitionSlice(keyword, null, null, 0, 10);

            // then
            for(int i = 0; i < expectedSlice.getContent().size(); i++) {
                log.info("expectedSlice = {}", expectedSlice.getContent().get(i));
            }
            assertEquals(expectedSlice, actualSlice);
            log.info("actualSlice = {}", actualSlice.getNumberOfElements());
        }
        @Test
        @DisplayName("대회 목록 조회시 큰 상금 기준으로 필터링을 적용할 수 있다.")
        public void 대회_상금_필터링() {
            // given
            String keyword = null;
            List<String> filterTypePrize = new ArrayList<>();
            filterTypePrize.add("totalPrize");
            Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdDate"));
            Slice<Competition> expectedSlice = new SliceImpl<>(competitions.stream().filter(competition -> competition.getTotalPrize() >= 100000).collect(Collectors.toList()), pageable, false);

            // when
            Mockito.when(competitionRepository.findCompetitionBySlice(keyword, filterTypePrize, pageable)).thenReturn(expectedSlice);
            Slice<Competition> actualSlice = competitionService.getCompetitionSlice(keyword, filterTypePrize, null, 0, 10);

            // then
            assertEquals(expectedSlice, actualSlice);
            for (int i = 0; i < expectedSlice.getContent().size(); i++) {
                log.info("expectedSlice = {}", expectedSlice.getContent().get(i));
            }
            log.info("expectedSlice = {}", expectedSlice);
            log.info("actualSlice = {}", actualSlice);

        }
        @Test
        @DisplayName("대회 목록 조회시 모집 마감일 기준으로 필터링을 적용할 수 있다.")
        public void 대회_마감일_필터링() {
            // given
            String keyword = null;
            List<String> filterTypeRecuitingEnd = new ArrayList<>();
            filterTypeRecuitingEnd.add("recruitingEnd");
            LocalDateTime now = LocalDateTime.parse("2023-03-20T00:00:00");
            Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdDate"));
            Slice<Competition> expectedSlice = new SliceImpl<>(competitions.stream().filter(competition -> competition.getRecruitingEnd().isAfter(now) && competition.getRecruitingEnd().isBefore(now.plusDays(7))).collect(Collectors.toList()), pageable, false);
            log.info("expectedSlice = {}", expectedSlice);

            // when
            Mockito.when(competitionRepository.findCompetitionBySlice(keyword, filterTypeRecuitingEnd, pageable)).thenReturn(expectedSlice);
            Slice<Competition> actualSlice = competitionService.getCompetitionSlice(keyword, filterTypeRecuitingEnd, null, 0, 10);

            // then
            assertEquals(expectedSlice, actualSlice);
            log.info("expectedSlice = {}", expectedSlice);
            log.info("actualSlice = {}", actualSlice);
            for(int i = 0; i < expectedSlice.getContent().size(); i++) {
                log.info("expectedSlice = {}", expectedSlice.getContent().get(i));
            }
        }
        @Test
        @DisplayName("대회 목록 조회시 추천 기준으로 필터링을 적용할 수 있다.")
        public void 대회_추천_필터링() {
            // given
            String keyword = null;
            List<String> filterTypeRecommend = new ArrayList<>();
            filterTypeRecommend.add("recommend");
            Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdDate"));
            Slice<Competition> expectedSlice = new SliceImpl<>(competitions.stream().filter(competition -> competition.getCompetitionType().equals(CompetitionType.PREMIUM)||competition.getCompetitionType().equals(CompetitionType.VIP)).collect(Collectors.toList()), pageable, false);

            // when
            Mockito.when(competitionRepository.findCompetitionBySlice(keyword, filterTypeRecommend, pageable)).thenReturn(expectedSlice);
            Slice<Competition> actualSlice = competitionService.getCompetitionSlice(keyword, filterTypeRecommend, null, 0, 10);

            // then
            assertEquals(expectedSlice, actualSlice);
            log.info("expectedSlice = {}", expectedSlice);
            log.info("actualSlice = {}", actualSlice);

        }

        private CompetitionDto getCompetitionDto(Member host, LocalDateTime recruitingStart, LocalDateTime recruitingEnd, LocalDateTime startDate) {
            CompetitionDto dto = CompetitionDto.builder().name("대회이름").host(host).sportCategory(SportCategory.ARM_WRESTLING).totalPrize(10000)
                    .content("대회내용").location("대회장소").locationDetail("대회장소상세").startDate(startDate) // dto.state 없이 빌드
                    .recruitingStart(recruitingStart).recruitingEnd(recruitingEnd).competitionType(CompetitionType.FREE).build();
            return dto;
        }

        @Test
        @DisplayName("주최자는 자신이 개최한 대회를 조회할 수 있다.")
        public void 주최자_대회_조회() {
            // given
            Member host = members.get(4);
            List<Competition> expectedCompetitions = competitions.stream().filter(competition -> competition.getHost().equals(host)).collect(Collectors.toList());
            Slice<Competition> expectedSlice = new SliceImpl<>(expectedCompetitions, PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdDate")), false);
            // when
            Mockito.when(competitionRepository.findCompetitionsBySliceWithHostUid(host.getUid(), PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdDate")))).thenReturn(expectedSlice);
            List<Competition> actualCompetitions = competitionService.getCompetitionSliceByHostId(host.getUid(), 0, 10).getContent();

            // then
            assertEquals(expectedCompetitions, actualCompetitions);
            for (int i = 0; i < expectedCompetitions.size(); i++) {
                log.info("expectedCompetitions = {}", expectedCompetitions.get(i));
            }
        }
    }

    /*
    대회 조회 점검 사항
    1. 대회 조회 시 대회 상태에 따라 조회되는 대회가 다르다. (PLANNING, RECRUITING, RECRUITING_END, IN_PROGRESS, END, SUSPEND)
    2. 대회 조회 시 대회 종목으로 조회할 수 있다. O
    3. 대회 조회 시 대회 이름으로 조회할 수 있다. O
    4. 대회 조회 시 대회 주최자로 조회할 수 있다. O
    5. 대회 조회 시 대회
     */

    /*
    대회 수정 점검 사항
    1. 대회 시작일 이후에는 대회 정보를 수정할 수 없다.

     */
}