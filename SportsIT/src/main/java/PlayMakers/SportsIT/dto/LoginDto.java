package PlayMakers.SportsIT.dto;

import lombok.*;

@Getter @Setter @Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginDto {

    //@NotNull  // 유효성 종속성 추가 필요
    //@Size(min = 4, max = 50)
    private String loginId;
    private String pw;
}

