package PlayMakers.SportsIT.auth.service;

import PlayMakers.SportsIT.auth.security.PrincipalDetails;
import PlayMakers.SportsIT.member.domain.Member;
import PlayMakers.SportsIT.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class PrincipalDetailsService implements UserDetailsService {

    @Autowired
    private MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {
        Member memberEntity = memberRepository.findByLoginId(loginId);

        if (memberEntity == null) {
            throw new UsernameNotFoundException("User not found.");
        }

        return new PrincipalDetails(memberEntity);
    }
}
