package PlayMakers.SportsIT.service;

import PlayMakers.SportsIT.auth.security.SecurityUtil;
import PlayMakers.SportsIT.domain.Feed;
import PlayMakers.SportsIT.domain.HostProfile;
import PlayMakers.SportsIT.domain.Member;
import PlayMakers.SportsIT.dto.MemberDto;
import PlayMakers.SportsIT.domain.MemberType;
import PlayMakers.SportsIT.exceptions.competition.IllegalMemberTypeException;
import PlayMakers.SportsIT.repository.MemberRepository;
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
    public Member join(MemberDto dto){
        // 같은 이름을 갖는 중복 회원은 X
        validateDuplicateMember(dto);  // 중복 회원 검증

        String memberTypeString = dto.getMemberType();

        MemberType memberType = MemberType.builder()
                .roleName(memberTypeString)
                .build();
        log.info("memberType: {}", memberType);
        Member member = Member.builder()
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
        if (memberRepository.findOneWithMemberTypeByEmail(dto.getEmail()).orElse(null) != null) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    public Optional<Member> getMemberWithMemberTypeByLoginId(String loginId) {
        return memberRepository.findOneWithMemberTypeByEmail(loginId);
    }

    public Optional<Member> getMyMemberWithMemberType() {
        return SecurityUtil.getCurrentLoginId().flatMap(memberRepository::findOneWithMemberTypeByEmail);
    }

    /**
     * 전체 회원 조회
     */
    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    public List<Member> findByName(String name){return memberRepository.findByName(name);}

    public Member findOne(String memberId) {
        return memberRepository.findByEmail(memberId);
    }

    public Boolean isDuplicateEmail(String email) {
        return memberRepository.existsByEmail(email);
    }
    public List<Feed> getAllFeedsByMember(Member member) {
        return member.getFeeds();
    }
    public HostProfile getHostProfileByMember(Member host) {
        if (!host.getMemberType().stream().anyMatch(memberType ->
                memberType.getRoleName().equals("ROLE_INSTITUTION"))) {
            throw new IllegalMemberTypeException("주최자 계정이 아닙니다.");
        }
        return host.getHostProfile();
    }
}
