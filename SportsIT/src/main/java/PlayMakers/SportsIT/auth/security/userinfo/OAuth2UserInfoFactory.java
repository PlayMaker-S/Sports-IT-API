package PlayMakers.SportsIT.auth.security.userinfo;

import PlayMakers.SportsIT.auth.security.enums.AuthProvider;

import java.util.Map;

public class OAuth2UserInfoFactory {

    public static OAuth2UserInfo getOAuth2UserInfo(AuthProvider authProvider, Map<String, Object> attributes) {
        switch (authProvider) {
            case GOOGLE: return new GoogleOAuth2UserInfo(attributes);
            case KAKAO: return new KakaoOAuth2UserInfo(attributes);

            default: throw new IllegalArgumentException("Invalid Provider Type.");
        }
    }
}
