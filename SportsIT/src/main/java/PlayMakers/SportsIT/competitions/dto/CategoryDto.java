package PlayMakers.SportsIT.competitions.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
public class CategoryDto {
    public static class Request{
        private String category;
        private String name;
    }

    public static class Req {
        private Long code;
    }

    public static class Res {
        private Long code;
        private String name;
    }


}
