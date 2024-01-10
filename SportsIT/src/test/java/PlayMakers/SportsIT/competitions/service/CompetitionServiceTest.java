package PlayMakers.SportsIT.competitions.service;

import PlayMakers.SportsIT.competitions.domain.Category;
import PlayMakers.SportsIT.competitions.domain.Competition;
import PlayMakers.SportsIT.competitions.enums.CompetitionState;
import PlayMakers.SportsIT.competitions.repository.CategoryRepository;
import PlayMakers.SportsIT.config.TestConfig;
import PlayMakers.SportsIT.domain.Member;
import PlayMakers.SportsIT.domain.MemberType;
import PlayMakers.SportsIT.competitions.repository.CompetitionRepository;
import PlayMakers.SportsIT.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ExtendWith(SpringExtension.class)
@Import(TestConfig.class)
@Slf4j
class CompetitionServiceTest {
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    CompetitionRepository competitionRepository;
    @Autowired
    CategoryRepository categoryRepository;
    CompetitionService competitionService;

    public static Member member =  Member.builder()
            .email("test")
                .pw("1234")
                .name("test_user_1")
                .phone("010-1111-1111")
                .memberType(Set.of(MemberType.builder().roleName("ROLE_INSTITUTION").build()))
                .build();

    @BeforeEach
    void setUp() {
        member = memberRepository.save(member);
        competitionService = new CompetitionServiceImpl_v2(competitionRepository);
    }

    @AfterEach
    void tearDown() {
        competitionRepository.findAll().forEach(
                createMockCompetition().getCategories()::remove
        );
        competitionRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @Nested
    class 대회_생성_테스트 {
        @DisplayName("주최자는 대회를 생성할 수 있다.")
        @Test
        void 대회_생성_테스트() {
            //given
            // 종목 조회
            Set<Category> categories = Set.of(categoryRepository.findByCode(1L).get()); // 팔씨름
            // 대회 생성
            Competition competition = createMockCompetition();
            //when
            Competition created = competitionService.createCompetition(member, competition, categories);

            competitionService.updateCompetition(created.getCompetitionId(), created);

            //then
            Assertions.assertNotNull(created);
            log.info("대회 생성: {}", created);
            log.info(created.getCategories().stream().map(Category::getName).reduce((a, b) -> a + ", " + b).orElse("null"));
        }
    }

    @Nested
    class 대회_수정_테스트 {
        @DisplayName("주최자는 대회를 수정할 수 있다.")
        @Test
        void 대회_수정_테스트() {
            //given
            // 종목 조회
            Set<Category> categories = Set.of(categoryRepository.findByCode(1L).get()); // 팔씨름
            // 대회 생성
            Competition competition = createMockCompetition();
            //when
            Competition created = competitionService.createCompetition(member, competition, categories);
            competition.setName("test2");
            competitionService.updateCompetition(created.getCompetitionId(), competition);
            Competition updated = competitionRepository.findByCompetitionId(created.getCompetitionId()).get();

            //then
            Assertions.assertEquals(competition.getName(), updated.getName());
        }
    }

    @Nested
    class 대회_조회_테스트 {
        @Test
        @DisplayName("대회 ID로 대회를 조회할 수 있다.")
        void 대회_단일_조회_테스트() {
            //given
            // 종목 조회
            Set<Category> categories = Set.of(categoryRepository.findByCode(1L).get()); // 팔씨름
            // 대회 생성
            Competition competition = createMockCompetition();
            //when
            Competition created = competitionService.createCompetition(member, competition, categories);
            Competition found = competitionService.findCompetition(created.getCompetitionId());

            //then
            Assertions.assertEquals(created.getCompetitionId(), found.getCompetitionId());
        }

        @Test
        @DisplayName("주최자 ID로 대회를 조회할 수 있다.")
        void 주최자ID_대회_조회_테스트() {
            //given
            // 종목 조회
            Set<Category> categories = Set.of(categoryRepository.findByCode(1L).get()); // 팔씨름
            // 대회 생성
            Competition competition = createMockCompetition();
            //when
            Competition created = competitionService.createCompetition(member, competition, categories);
            List<Competition> founds = competitionService.findCompetitionsWithHostId(member.getUid(), 0, 10).stream().toList();

            //then
            Assertions.assertEquals(created.getCompetitionId(), founds.get(0).getCompetitionId());
        }

    }
    @Nested
    class 대회_삭제_테스트 {
        @Test
        @DisplayName("대회 ID로 대회를 삭제할 수 있다.")
        void 대회_삭제_테스트() {
            //given
            // 종목 조회
            Set<Category> categories = Set.of(categoryRepository.findByCode(1L).get()); // 팔씨름
            // 대회 생성
            Competition competition = createMockCompetition();
            //when
            Competition created = competitionService.createCompetition(member, competition, categories);
            competitionService.deleteCompetition(created.getCompetitionId());

            //then
            Assertions.assertFalse(competitionRepository.findByCompetitionId(created.getCompetitionId()).isPresent());
        }
    }

    @Test
    void findCompetitionsWithConditions() {
    }


    Competition createMockCompetition() {
        return Competition.builder()
                .name("test")
                .management("대한팔씨름연맹(KAF)")
                .content("test")
                .templateID("1")
                .location("test")
                .locationDetail("test")
                .recruitingStart(LocalDateTime.of(2021, 8, 1, 0, 0, 0))
                .recruitingEnd(LocalDateTime.of(2021, 8, 2, 0, 0, 0))
                .startDate(LocalDateTime.of(2021, 8, 3, 0, 0, 0))
                .endDate(LocalDateTime.of(2021, 8, 4, 0, 0, 0))
                .state(CompetitionState.END)
                .latitude(37.123456)
                .longitude(127.123456)
                .prizeDetail("시상 정보")
                .totalPrize(1000000)
                .maxPlayer(100)
                .maxViewer(1000)
                .build();
    }
}