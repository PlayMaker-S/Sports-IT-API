package PlayMakers.SportsIT.auth.security;

import PlayMakers.SportsIT.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Slf4j
public class PrincipalUser extends User {
    private Member member;

    //public PrincipalDetails(Member member) {
    //    this.member = member;
    //}

    public PrincipalUser(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
    }

    public PrincipalUser(String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
    }

    // 해당 User의 권한을 반환
//    @Override
//    public Collection<GrantedAuthority> getAuthorities() {
//        Collection<GrantedAuthority> collect = new ArrayList<>();
//        // 권한(MemberType)을 리턴
//        collect.add((GrantedAuthority) () -> member.getMemberType().toString());
//        return null;
//    }

    // User 의 password 리턴
    @Override
    public String getPassword() {
        return "{noop}" + member.getPw();
    }

    @Override
    public String getUsername() {
        return member.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {

        // 사이트 내에서 일정 기간안 로그인을 안하면 휴먼계정을 전환을 하도록 하겠다.
        // -> loginDate 타입을 모아놨다가 이 값을 false로 return 해버리면 된다.
        return true;
    }
}
