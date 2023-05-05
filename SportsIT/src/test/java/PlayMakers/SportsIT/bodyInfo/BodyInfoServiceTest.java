package PlayMakers.SportsIT.bodyInfo;

import PlayMakers.SportsIT.config.TestConfig;
import PlayMakers.SportsIT.domain.BodyInfo;
import PlayMakers.SportsIT.domain.Competition;
import PlayMakers.SportsIT.domain.Member;
import PlayMakers.SportsIT.domain.MemberType;
import PlayMakers.SportsIT.dto.BodyInfoDto;
import PlayMakers.SportsIT.repository.BodyInfoRepository;
import PlayMakers.SportsIT.repository.MemberRepository;
import PlayMakers.SportsIT.service.BodyInfoService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@Slf4j
@ExtendWith(MockitoExtension.class)
class BodyInfoServiceTest {
    @InjectMocks
    BodyInfoService bodyInfoService;
    @Mock
    BodyInfoRepository bodyInfoRepository;
    @Mock
    MemberRepository memberRepository;

    MemberType userTypePlayer = MemberType.builder()
            .roleName("ROLE_PLAYER")
            .build();

    @Test
    void 신체정보생성() {
        //given
        Long memberId = 1L;
        Member member = Member.builder()
                .uid(memberId)
                .pw("1234")
                .name("홍길동1")
                .memberType(Collections.singleton(userTypePlayer))
                .email("test@google.com")
                .phone("010-1234-8765")
                .build();


        BodyInfoDto dto = BodyInfoDto.builder()
                .member(member)
                .height(170.0f)
                .weight(65.0f)
                .fatMass(12.3f)
                .smMass(33.2f)
                .build();
        BodyInfo mockBodyInfo = dto.toEntity();

        memberRepository.save(member);

        //mocking
        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
        given(bodyInfoRepository.save(any(BodyInfo.class))).willReturn(mockBodyInfo);

        //when
        BodyInfo createdBodyInfo = bodyInfoService.create(dto);

        //then
        assertNotNull(createdBodyInfo);
    }

    @Test
    void 신체정보조회() {
        //given
        Long memberId = 1L;
        Member member = Member.builder()
                .uid(memberId)
                .pw("1234")
                .name("홍길동1")
                .memberType(Collections.singleton(userTypePlayer))
                .email("test@google.com")
                .phone("010-1234-8765")
                .build();

        BodyInfoDto dto = BodyInfoDto.builder()
                .member(member)
                .height(170.0f)
                .weight(65.0f)
                .fatMass(12.3f)
                .smMass(33.2f)
                .build();
        BodyInfo mockBodyInfo = dto.toEntity();

        memberRepository.save(member);
        bodyInfoRepository.save(mockBodyInfo);

        //mocking
        given(bodyInfoRepository.findByMemberUid(memberId)).willReturn(Optional.ofNullable(mockBodyInfo));

        //when
        Optional<BodyInfo> getBodyInfo = bodyInfoService.getBodyInfo(member);

        //then
        log.info(getBodyInfo.get().getMember().getName());
        assertThat(mockBodyInfo).isEqualTo(getBodyInfo.get());
    }
}