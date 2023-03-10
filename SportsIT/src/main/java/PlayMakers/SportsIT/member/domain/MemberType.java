package PlayMakers.SportsIT.member.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberType {
    PLAYER("선수"),
    INSTITUTION("단체");

    private final String value;
}
