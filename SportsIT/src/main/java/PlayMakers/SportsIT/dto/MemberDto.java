package PlayMakers.SportsIT.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberDto {
    @Schema(description = "사용자 아이디, 회원가입시 필요 X", example = "sportsit_admin2@abc.com")
    private String loginId;
    @Schema(description = "비밀번호", example = "1234")
    private String pw;
    @Schema(description = "사용자 이름", example = "스포츠잇 관리자")
    private String name;
    @Schema(description = "사용자 이메일", example = "sportsit_admin2@abc.com")
    private String email;
    @Schema(description = "전화번호", example = "010-9797-7979")
    private String phone;
    @Schema(description = "사용자 소개, 회원가입시 필요 없음", example = "스포츠잇 관리자입니다.")
    private String description;
    @Schema(description = "활성계정 여부, 회원가입시 자동으로 true 설정", example = "true")
    private boolean activated;
    @Schema(description = "회원 타입, 체육인/주최자", example = "ROLE_PLAYER")
    private String memberType;
    @Schema(description = "관심 종목", example = "[\"SOCCER\", \"HOCKEY\", \"RUNNING\"]")
    private List<String> categories;

    @Data
    @AllArgsConstructor @NoArgsConstructor
    @Builder
    public static class Summary {
        private Long uid;
        private String name;
    }
}
