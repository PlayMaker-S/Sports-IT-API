package PlayMakers.SportsIT.service;

import PlayMakers.SportsIT.auth.security.SecurityUtil;
import PlayMakers.SportsIT.domain.*;
import PlayMakers.SportsIT.dto.MemberDto;
import PlayMakers.SportsIT.exceptions.competition.IllegalMemberTypeException;
import PlayMakers.SportsIT.repository.CategoryRepository;
import PlayMakers.SportsIT.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;
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

        Set<Category> categories = new HashSet<>();
        if (dto.getCategories() == null) {
            dto.setCategories(new ArrayList<>(){{add("ETC");}});
        }
        for (String category : dto.getCategories()) {
            categories.add(categoryRepository.findById(category).get());
        }
        log.info("memberType: {}", memberType);
        Member member = Member.builder()
                .pw(passwordEncoder.encode(dto.getPw()))
                .name(dto.getName())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .description(dto.getDescription())
                .memberType(Collections.singleton(memberType))
                .activated(true)
                .categories(categories)
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

    public Boolean isDuplicatedEmail(String email) {
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

    public boolean isDuplicatedPhone(String phoneNumber) { return memberRepository.existsByPhone(phoneNumber); }

    public String findEmailByPhone(String phoneNumber) {
        Member member = memberRepository.findByPhone(phoneNumber).orElse(null);
        if (member == null) {
            return null;
        }
        return member.getEmail();
    }

    public boolean isExistsWithPhoneAndEmail(String email, String phone) {
        Member member = memberRepository.findByEmail(email);
        if (member == null) {
            return false;
        }
        return member.getPhone().equals(phone);
    }

    public String generateNewPassword(String email) {
        // 랜덤 비밀번호 생성
        String newPassword = UUID.randomUUID().toString().substring(0, 9);

        // ! @ # 중에서 랜덤으로 하나를 선택 후 newPassword의 임의의 위치에 삽입
        char specialChar = "!@#".charAt((int) (Math.random() * 3));
        int index = (int) (Math.random() * 8);
        newPassword = newPassword.substring(0, index) + specialChar + newPassword.substring(index);

        // 비밀번호 변경
        Member member = memberRepository.findByEmail(email);
        member.setPw(passwordEncoder.encode(newPassword));
        return newPassword;
    }
}
