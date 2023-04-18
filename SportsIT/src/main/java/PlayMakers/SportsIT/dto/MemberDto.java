package PlayMakers.SportsIT.member.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberDto {
    private String loginId;
    private String pw;
    private String name;
    private String email;
    private String phone;
    private boolean activated;
    private String memberType;

    /* DTO -> Entity */
//    public Member toEntity() {
//        MemberType memberEntityType = null;
//        log.info("memberType = {}", memberType);
//
//        // memberType과 같은 MemberType을 찾아 반환
//        for (MemberType type : MemberType.values()) {
//            log.debug("type = {}", type.getValue());
//            log.debug(memberType);
//
//            if (type.getValue().equals(memberType)) {
//                memberEntityType = type;
//                break;
//            }
//        }
//
//        if (memberEntityType == null) {
//            throw new IllegalArgumentException("MemberType is not valid");
//        }
//
//        return Member.builder()
//                .loginId(loginId)
//                .pw(pw)
//                .name(name)
//                .memberType(memberEntityType)
//                .build();
//    }
}
