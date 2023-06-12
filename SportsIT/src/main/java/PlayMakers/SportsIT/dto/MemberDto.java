package PlayMakers.SportsIT.dto;

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
    private String loginId;
    private String pw;
    private String name;
    private String email;
    private String phone;
    private String description;
    private boolean activated;
    private String memberType;
    private List<String> categories;

    @Data
    @AllArgsConstructor @NoArgsConstructor
    @Builder
    public static class Summary {
        private Long uid;
        private String name;
    }
}
