package PlayMakers.SportsIT.auth.security.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {

    ADMIN("ROLE_ADMIN", "관리자"),
    PLAYER("ROLE_PLAYER", "체육인"),
    INSTITUTION("ROLE_INSTITUTION", "단체"),
    SHOP("ROLE_SHOP", "판매자");

    private final String key;
    private final String title;

}
