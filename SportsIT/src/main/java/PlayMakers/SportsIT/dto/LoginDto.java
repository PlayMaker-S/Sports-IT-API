package PlayMakers.SportsIT.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter @Setter @Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginDto {

    //@NotNull  // 유효성 종속성 추가 필요
    //@Size(min = 4, max = 50)
    @Schema(description = "사용자 이메일", example = "sportsit_admin@abc.com")
    private String loginId;
    @Schema(description = "비밀번호", example = "1234")
    private String pw;
}

