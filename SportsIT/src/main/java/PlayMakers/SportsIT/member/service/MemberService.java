package PlayMakers.SportsIT.member.service;

import PlayMakers.SportsIT.auth.security.SecurityUtil;
import PlayMakers.SportsIT.member.domain.Member;
import PlayMakers.SportsIT.member.domain.MemberDto;
import PlayMakers.SportsIT.member.domain.MemberType;
import PlayMakers.SportsIT.member.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 회원 가입
     */
    public Member join(MemberDto dto) {
        // 같은 이름을 갖는 중복 회원은 X
        validateDuplicateMember(dto);  // 중복 회원 검증

        String memberTypeString = dto.getMemberType();

        MemberType memberType = MemberType.builder()
                .roleName(memberTypeString)
                .build();
        log.info("memberType: {}", memberType);
        Member member = Member.builder()
                .loginId(dto.getLoginId())
                .pw(passwordEncoder.encode(dto.getPw()))
                .name(dto.getName())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .memberType(Collections.singleton(memberType))
                .activated(true)
                .build();

        return memberRepository.save(member);
    }

    private void validateDuplicateMember(MemberDto dto) {
        if (memberRepository.findOneWithMemberTypeByLoginId(dto.getLoginId()).orElse(null) != null) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    public Optional<Member> getMemberWithMemberTypeByLoginId(String loginId) {
        return memberRepository.findOneWithMemberTypeByLoginId(loginId);
    }

    public Optional<Member> getMyMemberWithMemberType() {
        return SecurityUtil.getCurrentLoginId().flatMap(memberRepository::findOneWithMemberTypeByLoginId);
    }

    /**
     * 전체 회원 조회
     */
    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    public Optional<Member> findByName(String name){return memberRepository.findByName(name);}

    public Member findOne(String memberId) {
        return memberRepository.findByLoginId(memberId);

    }
}
