package PlayMakers.SportsIT.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


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

    @Data
    @AllArgsConstructor @NoArgsConstructor
    @Builder
    public static class DeleteResponse {
        private String competitionName;
        private String sectorTitle;
        private String subSectorName;
    }
}
