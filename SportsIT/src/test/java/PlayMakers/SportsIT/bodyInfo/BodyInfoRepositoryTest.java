package PlayMakers.SportsIT.bodyInfo;

import PlayMakers.SportsIT.config.TestConfig;
import PlayMakers.SportsIT.domain.BodyInfo;
import PlayMakers.SportsIT.domain.Member;
import PlayMakers.SportsIT.domain.MemberType;
import PlayMakers.SportsIT.repository.BodyInfoRepository;
import PlayMakers.SportsIT.repository.MemberRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestConfig.class)
public class BodyInfoRepositoryTest {
    @Autowired
    MemberRepository memberRepository;

    @Autowired
    BodyInfoRepository bodyInfoRepository;

    MemberType userType = MemberType.builder()
            .roleName("ROLE_USER")
            .build();

    @BeforeEach
    public void before() {
        memberRepository.deleteAll();
        bodyInfoRepository.deleteAll();

        Member member = Member.builder()
                .pw("1234")
                .name("홍길동1")
                .memberType(Collections.singleton(userType))
                .email("test@google.com")
                .phone("010-1234-8765")
                .build();

        memberRepository.save(member);
    }

    @Test
    void 신체정보저장(){
        //given
        Member member = memberRepository.findByEmail("test@google.com");
        BodyInfo bodyInfo = BodyInfo.builder()
                .member(member)
                .height(170.0f)
                .weight(65.0f)
                .fatMass(12.3f)
                .smMass(33.2f)
                .build();
        log.info("BodyInfo's memberId : {}", bodyInfo.getMember().getUid());

        //when
        BodyInfo savedBodyInfo = bodyInfoRepository.save(bodyInfo);

        //then
        System.out.println("BodyInfo's member email : " + savedBodyInfo.getMember().getEmail());
        assertThat(bodyInfo).isEqualTo(savedBodyInfo);
    }
}