package PlayMakers.SportsIT.dto;

import PlayMakers.SportsIT.domain.CompetitionForm;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
public class CompetitionFormDto {
    @ArraySchema(arraySchema = @Schema(description = "부문", implementation = CompetitionForm.Sector.class))
    private List<CompetitionForm.Sector> sectors;
    private List<CompetitionForm.Answer> answers = null;
    @Schema(description = "VAT", example = "7000")
    private Long vat;
    @Schema(description = "수수료", example = "2100")
    private Long fee;
    @Schema(description = "보험료", example = "5000")
    private Long insurance;


    @Data
    @AllArgsConstructor @NoArgsConstructor
    @Schema(name = "Sector")
    public static class Sector{
        @Schema(description = "부문", example = "남자")
        private String title;
        @ArraySchema(arraySchema = @Schema(description = "체급", implementation = CompetitionForm.SubSector.class))
        private List<SubSector> subSectors;

    }

    @Data
    @AllArgsConstructor @NoArgsConstructor
    @Schema(name = "SubSector")
    public static class SubSector{
        @Schema(description = "체급", example = "-85kg")
        private String name;
        @Schema(description = "체크 여부", example = "true")
        private boolean checked = true;
    }

    @Data
    @AllArgsConstructor @NoArgsConstructor
    public static class Answer {
        private String question;
        private List<Opt> options;
    }
    @Data
    @AllArgsConstructor @NoArgsConstructor
    public static class Opt{
        private String opt;
    }

    public CompetitionForm toEntity(){
        return CompetitionForm.builder()
                .sectors(sectors)
                .answers(answers)
                .build();
    }

}
