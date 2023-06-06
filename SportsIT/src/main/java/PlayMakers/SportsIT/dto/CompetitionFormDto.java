package PlayMakers.SportsIT.dto;

import PlayMakers.SportsIT.domain.CompetitionForm;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
public class CompetitionFormDto {
    private List<CompetitionForm.Sector> sectors;
    private List<CompetitionForm.Answer> answers = null;
    private Long vat;
    private Long fee;
    private Long insurance;


    @Data
    @AllArgsConstructor @NoArgsConstructor
    public static class Sector{
        private String title;
        private List<SubSector> subSectors;

    }

    @Data
    @AllArgsConstructor @NoArgsConstructor
    public static class SubSector{
        private String name;
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
