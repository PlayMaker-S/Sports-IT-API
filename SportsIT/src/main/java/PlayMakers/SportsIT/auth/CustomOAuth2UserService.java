package PlayMakers.SportsIT.auth;

import PlayMakers.SportsIT.auth.security.PrincipalUser;
import PlayMakers.SportsIT.auth.security.userinfo.OAuth2UserInfo;
import PlayMakers.SportsIT.auth.security.userinfo.OAuth2UserInfoFactory;
import PlayMakers.SportsIT.auth.security.enums.Role;
import PlayMakers.SportsIT.domain.Member;
import PlayMakers.SportsIT.domain.MemberType;
import PlayMakers.SportsIT.exceptions.EntityNotFoundException;
import PlayMakers.SportsIT.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import PlayMakers.SportsIT.auth.security.enums.AuthProvider;

import java.util.Collections;

@Slf4j
@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        OAuth2UserService oAuth2UserService = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);

        return processOAuth2User(oAuth2UserRequest, oAuth2User);
    }
    /*
        사용자 정보 추출
     */
    private OAuth2User processOAuth2User(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User) {

        //OAuth2 로그인 플랫폼 구분
        AuthProvider authProvider = AuthProvider.valueOf(oAuth2UserRequest.getClientRegistration().getRegistrationId().toUpperCase());
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(authProvider, oAuth2User.getAttributes());
        log.info("Provider = {}", authProvider);
        if (!StringUtils.hasText(oAuth2UserInfo.getEmail())) {
            throw new RuntimeException("Email not found from OAuth2 provider");
        }

        //Member user = memberRepository.findByEmail(oAuth2UserInfo.getEmail()).orElse(null);
        Member user;

        try {
            user = memberRepository.findByEmail(oAuth2UserInfo.getEmail());
        } catch (EntityNotFoundException e) {
            user = null;
        }

        //이미 가입된 경우
        if (user != null) {
            if (user.getAuthProvider() == null || !user.getAuthProvider().equals(authProvider)) {
                throw new RuntimeException("Email already signed up.");
            }
            user = updateUser(user, oAuth2UserInfo);
        }
        //가입되지 않은 경우
        else {
            user = registerUser(authProvider, oAuth2UserInfo);
        }
        return PrincipalUser.create(user, oAuth2UserInfo.getAttributes());
    }

    private Member registerUser(AuthProvider authProvider, OAuth2UserInfo oAuth2UserInfo) {
        Member user = Member.builder()
                .email(oAuth2UserInfo.getEmail())
                .name(oAuth2UserInfo.getName())
                .phone("010-test" + oAuth2UserInfo.getOAuth2Id())
                .providerId(oAuth2UserInfo.getOAuth2Id())
                .authProvider(authProvider)
                .memberType(Collections.singleton(MemberType.builder().roleName(Role.PLAYER.getKey()).build()))
                .build();

        return user;
    }

    private Member updateUser(Member user, OAuth2UserInfo oAuth2UserInfo) {
        return memberRepository.save(user.update(oAuth2UserInfo));
    }

}
