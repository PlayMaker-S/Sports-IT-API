package PlayMakers.SportsIT.member.repository;

import PlayMakers.SportsIT.member.domain.Member;
import PlayMakers.SportsIT.member.domain.MemberType;
import jakarta.persistence.EntityManager;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest  //@Transactional 어노테이션 포함 -> 테스트가 끝나면 자동으로 롤백
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ExtendWith(SpringExtension.class)  // 필요한 의존성 추가 (@Autowired, @MockBean)
class MemberRepositoryTest {
    @Autowired
    MemberRepository memberRepository;

    @Autowired
    EntityManager em;

    MemberType userType = MemberType.builder()
            .roleName("ROLE_USER")
            .build();

    @Before
    public void before() {
        memberRepository.deleteAll();

        Member member1 = Member.builder()
                .loginId("test1@gmail.com")
                .pw("1234")
                .name("홍길동")
                .memberType(Collections.singleton(userType))
                .email("test@google.com")
                .phone("010-1234-5678")
                .build();
        Member member2 = Member.builder()
                .loginId("test2@naver.com")
                .pw("1234")
                .name("홍길동")
                .memberType(Collections.singleton(userType))
                .email("test2@naver.com")
                .phone("010-1111-2222")
                .build();
        Member member3 = Member.builder()
                .loginId("010-1234-5678")
                .pw("1234")
                .name("홍길동")
                .memberType(Collections.singleton(userType))
                .email("test@google.com")
                .phone("010-3333-3333")
                .build();

        memberRepository.save(member1);
        memberRepository.save(member2);
        memberRepository.save(member3);
    }

    //@AfterEach
    //void afterEach() {
    //    memberRepository.clearStore();
    //}

    @Test
    void 사용자_저장() {
        //given
        Member member = Member.builder()
                .uid(1L)
                .loginId("test4@gmail.com")
                .pw("1234")
                .name("홍길동")
                .memberType(Collections.singleton(userType))
                .email("test4@gmail.com")
                .phone("010-1234-5678")
                .build();

        //when
        Member savedMember = memberRepository.save(member);

        //then
        Member findMember = memberRepository.findById(savedMember.getUid()).get();

        //System.out.println("savedMember = " + savedMember);
        //System.out.println("findMember = " + findMember);
        assertThat(findMember).isEqualTo(savedMember);
    }

    @Test
    void 사용자_정보_수정() {
        //given
        Member member = new Member(1L, "test_id", "test", "홍길동", "abcd@ajou.ac.kr", "010-1234-5678", true, Collections.singleton(userType));

        Member savedMember = memberRepository.save(member);
        Long savedId = savedMember.getUid();
        System.out.println("savedMember = " + savedMember + ", password = " + savedMember.getPw() + ", name = " + savedMember.getName());
        //when
        Member member2 = new Member(2L, "test_id2", "test", "홍길동", "abcd@ajou.ac.kr", "010-1234-5678", true, Collections.singleton(userType));
        //memberRepository.update(savedId, updateParam);

        Member findMember = memberRepository.findById(savedId).get();
        System.out.println("findMember = " + findMember + ", password = " + findMember.getPw() + ", name = " + findMember.getName());
        //then
        //assertThat(findMember.getPw()).isEqualTo(updateParam.getPw());
        //assertThat(findMember.getName()).isEqualTo(updateParam.getName());


    }
}