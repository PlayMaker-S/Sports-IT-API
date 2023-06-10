package PlayMakers.SportsIT.dto;

import lombok.Builder;
import lombok.Data;


public class ParticipantDto {
    @Data
    @Builder
    public static class Response {
        private String userName;
        private Long uid;
        private String phone;
        private String sectorTitle;
        private String subSectorName;
        @Builder.Default
        private String profileImg = "";
    }
}
