package PlayMakers.SportsIT.competition;

import PlayMakers.SportsIT.config.TestConfig;
import PlayMakers.SportsIT.domain.*;
import PlayMakers.SportsIT.competitions.domain.Competition;
import PlayMakers.SportsIT.competitions.repository.CompetitionRepository;
import PlayMakers.SportsIT.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.Collections;

import static java.lang.Thread.sleep;
import static org.assertj.core.api.Assertions.*;

@Slf4j
@ExtendWith(SpringExtension.class)  // 필요한 의존성 추가 (@Autowired, @MockBean)
@DataJpaTest  //@Transactional 어노테이션 포함 -> 테스트가 끝나면 자동으로 롤백
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestConfig.class)
class CompetitionRepositoryTest {
    @Autowired
    CompetitionRepository competitionRepository;

    @Autowired
    MemberRepository memberRepository;

    MemberType hostType = MemberType.builder()
            .roleName("ROLE_INSTITUTION")
            .build();

    @BeforeEach
    public void before() {
        competitionRepository.deleteAll();
        // 호스트 생성
        Member member = createHost("test99@gmail.com", "010-1234-5678");

        // 대회 생성
        Competition competition = createCompetition(member, SportCategory.SOCCER);
        memberRepository.save(member);
        competitionRepository.save(competition);
    }
    public Member createHost(String email, String phone) {
        Member member = Member.builder()
                .pw("1234")
                .name("테스트")
                .memberType(Collections.singleton(hostType))
                .email(email)
                .phone(phone)
                .build();
        return member;
    }
    public Competition createCompetition(Member host, SportCategory category) {
        Competition competition = Competition.builder()
                .name("테스트대회_0")
                .startDate(LocalDateTime.of(2023, 4, 30, 0, 0))
                .endDate(LocalDateTime.of(2023, 5, 4, 0, 0))
                .recruitingStart(LocalDateTime.of(2023, 4, 20, 0, 0))
                .recruitingEnd(LocalDateTime.of(2023, 4, 25, 0, 0))
                .totalPrize(10000)
                .content("테스트 대회입니다.")
                .location("아주대학교")
                .locationDetail("체육관")
                .category(category)
                .host(host)
                .build();
        return competition;
    }

    @AfterEach
    public void after() {
        competitionRepository.deleteAll();
    }

    @Test
    @DisplayName("대회를 게시할 수 있다.")
    void 대회저장() {
        //given
        Member host = createHost("test123@gmail.com", "010-4321-8765");
        memberRepository.save(host);

        //when
        Competition competition = createCompetition(host, SportCategory.SOCCER);
        competitionRepository.save(competition);

        //then
        Competition findCompetition = competitionRepository.findById(competition.getCompetitionId()).orElseThrow(() -> new IllegalArgumentException("해당 대회가 존재하지 않습니다."));
        assertThat(findCompetition).isEqualTo(competition);

    }

    @Test
    @DisplayName("대회를 모두 조회할 수 있다.")
    void 대회모두조회(){
        //given
        int num_of_competition = competitionRepository.findAll().size();

        //when
        Member host = createHost("test123@gmail.com", "010-4321-8765");
        memberRepository.save(host);
        Competition competition = createCompetition(host, SportCategory.SOCCER);
        competitionRepository.save(competition);

        //then
        assertThat(competitionRepository.findAll().size()).isEqualTo(num_of_competition + 1);
    }

    @Test
    @DisplayName("대회를 아이디로 조회할 수 있다.")
    void 대회아이디로조회(){
        //given
        Competition competition = competitionRepository.findAll().get(0);

        //when

        //then
        Assert.assertNotNull(competition);
    }

    @Test
    @DisplayName("대회를 삭제할 수 있다.")
    void 대회삭제(){
        //given
        Competition competition = competitionRepository.findAll().get(0);
        long competitionId = competition.getCompetitionId();

        //when
        competitionRepository.delete(competition);

        //then
        Assertions.assertThrows(IllegalAccessException.class, () -> {
            competitionRepository.findById(competitionId).orElseThrow(() -> new IllegalAccessException("해당 대회가 존재하지 않습니다."));
        });
    }

    @Test
    @DisplayName("대회를 수정할 수 있다.")
    void 대회수정(){
        //given
        Competition competition = competitionRepository.findAll().get(0);
        long competitionId = competition.getCompetitionId();
        String oldName = competition.getName();
        LocalDateTime oldModifiedDate = competition.getUpdatedDate();
        LocalDateTime oldCreatedDate = competition.getCreatedDate();
        try {
            sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //when
        competition.setName("테스트대회_1");
        //competitionRepository.save(competition);

        //then
        Competition findCompetition = competitionRepository.findById(competitionId).orElseThrow(() -> new IllegalArgumentException("해당 대회가 존재하지 않습니다."));
        assertThat(findCompetition.getName()).isEqualTo("테스트대회_1");
        assertThat(findCompetition.getStartDate()).isEqualTo(competition.getStartDate());
        assertThat(findCompetition.getName()).isNotEqualTo(oldName);  // 대회 명이 수정되어야 한다.
        System.out.println("oldCreatedDate = " + oldCreatedDate + ", oldModifiedDate = " + oldModifiedDate);
        System.out.println("newCreatedDate = " + findCompetition.getCreatedDate() + ", newModifiedDate = " + findCompetition.getUpdatedDate());
    }

    @Nested
    @DisplayName("대회 Slice 테스트")
    class 대회_Slice_조회_테스트 {
        @Test
        @DisplayName("대회를 슬라이싱하여 조회할 수 있다.")
        void 대회_슬라이싱_조회() {
            //given
            int page = 0;
            int size = 10;
            Pageable pageable = PageRequest.of(page, size);
            String keyword = "";

            //when
            Slice<Competition> competitions = competitionRepository.findCompetitionBySlice(keyword, null, pageable);
            log.info("대회 개수 : " + competitions.getNumberOfElements());
            log.info("응답 : {}", competitions.getContent());

            //then
            assertThat(competitions.getNumberOfElements()).isEqualTo(1);
            assertThat(competitions.getContent().get(0).getName()).isEqualTo("테스트대회_0");
        }
    }


}

