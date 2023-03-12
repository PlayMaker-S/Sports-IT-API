package PlayMakers.SportsIT.member.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberType {
    선수("ROLE_USER"),
    단체("ROLE_INSTITUTION"),
    관리자("ROLE_ADMIN");

    private final String value;
}
