package PlayMakers.SportsIT.common;

import PlayMakers.SportsIT.domain.Member;
import PlayMakers.SportsIT.domain.MemberType;
import PlayMakers.SportsIT.dto.MemberDto;
import PlayMakers.SportsIT.repository.MemberRepository;
import PlayMakers.SportsIT.service.MemberService;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)  // 실제 DB로 테스트
//@RunWith(SpringJUnit4ClassRunner.class)
@ExtendWith(SpringExtension.class)
public class BaseEntityTest {
    @Autowired
    MemberRepository memberRepository;

    @Autowired
    MemberService memberService;
    MemberType userType = MemberType.builder()
            .roleName("ROLE_USER")
            .build();

    @Autowired
    EntityManager em;

    @AfterEach @Transactional
    public void after() {
        memberRepository.deleteAll();
    }

    @Test
    @DisplayName("BaseEntity를 상속받은 Entity는 CreatedDate가 자동생성 된다.")
    @Transactional
    public void createdDateTest(){
        // given
        MemberDto memberDto = MemberDto.builder()
                .pw("1234")
                .name("홍길동")
                .memberType("ROLE_USER")
                .email("test02@google.com")
                .phone("010-1234-5678")
                .build();

        // when
        Member member = memberService.join(memberDto);
        LocalDateTime createdAt = member.getCreatedDate();

        // then
        assertThat(createdAt).isNotNull();
    }

    @Test
    @DisplayName("BaseEntity를 상속받은 Entity는 ModifiedDate가 자동갱신 된다.")
    @Transactional
    public void modifiedDateTest() throws InterruptedException {
        // given
        MemberDto memberDto = MemberDto.builder()
                .pw("1234")
                .name("홍길동")
                .memberType("ROLE_USER")
                .email("test02@google.com")
                .phone("010-1234-5678")
                .build();

        // when
        Member member = memberService.join(memberDto);
        String email = member.getEmail();
        LocalDateTime oldUpdatedAt = member.getUpdatedDate();

        // when
        Thread.sleep(1000);
        member.setPhone("010-1111-2222");
        Member updatedMember = memberService.findOne(email);

        LocalDateTime newUpdatedAt = updatedMember.getUpdatedDate();

        // then
        assertThat(newUpdatedAt).isAfter(oldUpdatedAt);
        assertThat(member.getUid()).isEqualTo(updatedMember.getUid());

    }
}
