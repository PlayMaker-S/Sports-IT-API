package PlayMakers.SportsIT.member.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberDto {
    private String loginId;
    private String pw;
    private String name;

    /* DTO -> Entity */
    public Member toPlayerEntity() {
        return Member.builder()
                .loginId(loginId)
                .pw(pw)
                .name(name)
                .memberType(MemberType.PLAYER)
                .build();
    }

    public Member toInstitutionEntity() {
        return Member.builder()
                .loginId(loginId)
                .pw(pw)
                .name(name)
                .memberType(MemberType.INSTITUTION)
                .build();
    }
}
