package PlayMakers.SportsIT.member.service;

import PlayMakers.SportsIT.member.domain.Member;
import PlayMakers.SportsIT.member.domain.MemberDto;
import PlayMakers.SportsIT.member.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    /**
     * 회원 가입
     */
    public Long join(MemberDto dto) {
        // 같은 이름을 갖는 중복 회원은 X
        //validateDuplicateMember(member);
        //dto.setPw(encoder.encode(dto.getPw()));
        dto.setPw(dto.getPw()); // 추후 인코드 기능 추가 예정
        Member newMember = memberRepository.save(dto.toPlayerEntity());
        return newMember.getUid();
    }

    private void validateDuplicateMember(Member member) {
        memberRepository.findByName(member.getName())
                .ifPresent(m -> {
                    throw new IllegalStateException("이미 존재하는 회원입니다.");
                });
    }

    /**
     * 전체 회원 조회
     */
    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    public Optional<Member> findOne(Long memberId) {
        return memberRepository.findById(memberId);

    }
}
