package PlayMakers.SportsIT.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor @NoArgsConstructor
@Builder
public class CustomCategoryDto {
    private Long customCategoryId;
    private String name;
    private Long memberUid;
    private String memberName;
}
