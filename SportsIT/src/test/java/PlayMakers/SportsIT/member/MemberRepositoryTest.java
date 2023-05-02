package PlayMakers.SportsIT.member;

import PlayMakers.SportsIT.config.TestConfig;
import PlayMakers.SportsIT.domain.Member;
import PlayMakers.SportsIT.domain.MemberType;
import PlayMakers.SportsIT.repository.MemberRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@Slf4j
@ExtendWith(SpringExtension.class)  // 필요한 의존성 추가 (@Autowired, @MockBean)
@DataJpaTest  //@Transactional 어노테이션 포함 -> 테스트가 끝나면 자동으로 롤백
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestConfig.class)
class MemberRepositoryTest {
    @Autowired EntityManager em;
    JPAQueryFactory queryFactory;

    @Autowired
    MemberRepository memberRepository;


    MemberType userType = MemberType.builder()
            .roleName("ROLE_USER")
            .build();

    @BeforeEach
    public void before() {
        queryFactory = new JPAQueryFactory(em);
        memberRepository.deleteAll();

        Member member1 = Member.builder()
                .pw("1234")
                .name("홍길동1")
                .memberType(Collections.singleton(userType))
                .email("test@google.com")
                .phone("010-1234-8765")
                .build();
        Member member2 = Member.builder()
                .pw("1234")
                .name("홍길동2")
                .memberType(Collections.singleton(userType))
                .email("test2@naver.com")
                .phone("010-1111-2222")
                .build();
        Member member3 = Member.builder()
                .pw("1234")
                .name("홍길동3")
                .memberType(Collections.singleton(userType))
                .email("test222@google.com")
                .phone("010-3333-3333")
                .build();

        memberRepository.save(member1);
        memberRepository.save(member2);
        memberRepository.save(member3);
    }

    @AfterEach
    public void afterEach() {
        memberRepository.deleteAllInBatch();
    }

    @Test
    void 사용자_저장() {
        //given
        Member member = Member.builder()
                .pw("1234")
                .name("홍길동")
                .memberType(Collections.singleton(userType))
                .email("test4@gmail.com")
                .phone("010-1234-5678")
                .build();

        //when
        Member savedMember = memberRepository.save(member);
        Long savedId = savedMember.getUid();

        //then
        Member findMember = memberRepository.findById(savedId).get();

        //System.out.println("savedMember = " + savedMember);
        System.out.println("findMember = " + findMember.getUid() + " " + findMember.getEmail());
        assertThat(findMember).isEqualTo(savedMember);
    }

    @Test
    void 사용자_이름_검색(){
        //given
        Member member = Member.builder()
                .pw("1234")
                .name("11홍길동")
                .memberType(Collections.singleton(userType))
                .email("test4@gmail.com")
                .phone("010-1234-5678")
                .build();
        memberRepository.save(member);

        //when
        String containName = "홍길동";
        List<Member> memberList = memberRepository.findByNameContaining(containName);
        List<Member> findAllList = memberRepository.findAll();
        for (Member m : memberList){
            log.info("Name : {}", m.getName());
        }
        assertThat(findAllList).isEqualTo(memberList);

        //then

    }

    @Test
    void 사용자_정보_수정() {
        //given
        Member savedMember = memberRepository.findByEmail("test2@naver.com");
        Long savedId = savedMember.getUid();
        String oldPw = savedMember.getPw();

        //when
        String newPw = "4321";
        savedMember.setPw(newPw);
        Member refindMember = memberRepository.findById(savedId).get();

        //then
        // refindMember 속성 출력
        System.out.println("refindMember.getUid() = " + refindMember.getUid());
        System.out.println("refindMember.getEmail() = " + refindMember.getEmail());
        System.out.println("refindMember.getPw() = " + refindMember.getPw());
        System.out.println("refindMember.getName() = " + refindMember.getName());
        System.out.println("refindMember.getMemberType() = " + refindMember.getMemberType());
        assertThat(oldPw).isNotEqualTo(newPw); // 두 pw가 다른지 확인
        assertThat(newPw).isEqualTo(refindMember.getPw()); // 수정된 pw가 저장된 pw와 같은지 확인

//        //given
//        Member newMember = Member.builder()
//                .loginId("test_id")
//                .pw("test")
//                .name("홍길동")
//                .memberType(Collections.singleton(userType))
//                .email("abcd@ajou.ac.kr")
//                .phone("010-1234-5678")
//                .build();
//
//        Long savedId = memberRepository.save(newMember).getUid();  // 정상 실행
//        memberRepository.save(newMember).getUid();  // 정상 실행
//        Member findMember = memberRepository.findById(savedId).get();  // 저장된 회원 아이디 확인
//        System.out.println("findMember.getUid() = " + findMember.getUid());
//
//        //when
//        findMember.setPw("4321");
//        memberRepository.save(findMember); // 비밀번호 수정
//        //Member member2 = new Member(2L, "test_id2", "test", "홍길동", "abcd@ajou.ac.kr", "010-1234-5678", true, Collections.singleton(userType));
//        //memberRepository.update(savedId, updateParam);
//
//        //Member findMember = memberRepository.findById(savedId).get();
//        //System.out.println("findMember = " + findMember + ", password = " + findMember.getPw() + ", name = " + findMember.getName());
//
//        //then
//        //assertThat(findMember.getPw()).isEqualTo(updateParam.getPw());
//        //assertThat(findMember.getName()).isEqualTo(updateParam.getName());
//        Member updatedMember = memberRepository.findById(savedId).get();
//        System.out.println("updatedMember ID, PW = " + updatedMember.getUid() + ", " + updatedMember.getPw());
//        System.out.println("findMember ID, PW = " + findMember.getUid() + ", " + findMember.getPw());
//        assertThat(findMember.getPw()).isEqualTo(updatedMember.getPw());


    }
}