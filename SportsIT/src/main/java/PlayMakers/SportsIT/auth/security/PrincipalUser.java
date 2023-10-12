package PlayMakers.SportsIT.auth.security;

import PlayMakers.SportsIT.domain.Member;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.*;

@Slf4j
@Getter
public class PrincipalUser implements UserDetails, OAuth2User {
    //private Member member;
    private Long id;
    private String email;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;
    private Map<String, Object> attributes;

//    public PrincipalDetails(Member member) {
//        this.member = member;
//    }

    public PrincipalUser(Long id, String username, String password, Collection<? extends GrantedAuthority> authorities) {
        //super(username, password, authorities);
        this.id = id;
        this.email = username;
        this.password = password;
        this.authorities = authorities;

    }

    public static PrincipalUser create(Member member) {
        // Default로 새 계정 생성시 ROLE_PLAYER 권한을 준다.
        List<GrantedAuthority> authorities =
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));

        return new PrincipalUser(
                member.getUid(),
                member.getEmail(),
                member.getPw(),
                authorities
        );
    }

    // ROLE_ADMIN, ROLE_INSTITUTION 권한 부여 시 생성자
    public static PrincipalUser create(Member member, Map<String, Object> attributes) {
        PrincipalUser userPrincipal = PrincipalUser.create(member);
        userPrincipal.setAttributes(attributes);

        return userPrincipal;
    }
//
//    public PrincipalUser(String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
//        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
//    }

    // 해당 User의 권한을 반환
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
//        Collection<GrantedAuthority> collect = new ArrayList<>();
//        // 권한(MemberType)을 리턴
//        collect.add((GrantedAuthority) () -> member.getMemberType().toString());
//        return null;
        return authorities;
    }

    // User 의 password 리턴
    @Override
    public String getPassword() {
        return "{noop}" + password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getName() {
        return this.getUsername();
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

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }
}
