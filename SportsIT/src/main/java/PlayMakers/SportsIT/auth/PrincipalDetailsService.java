package PlayMakers.SportsIT.auth;

import PlayMakers.SportsIT.domain.Member;
import PlayMakers.SportsIT.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * Spring Security에서 사용자 정보를 가져오기 위한 인터페이스
 * 참고 : User는 UserDetails를 구현한 클래스, Spring Security에서 사용자 정보를 담는 인터페이스
 */
@RequiredArgsConstructor
@Service
public class PrincipalDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member memberEntity = memberRepository.findByEmail(email);

        return memberRepository.findOneWithMemberTypeByEmail(email)
                .map(member -> createUser(email, member))
                .orElseThrow(() -> new UsernameNotFoundException("User not found in DB."));
    }

    private User createUser(String loginId, Member member) {
        if (!member.isActivated()) {
            throw new RuntimeException(loginId + " -> 활성화 되어있지 않음");
        }
        List<GrantedAuthority> grantedAuthorities = member.getMemberType().stream()
                .map(memberType -> (GrantedAuthority) () -> memberType.getRoleName())
                .toList();
        return new User(
                member.getEmail(),
                member.getPw(),
                grantedAuthorities);
    }
}
