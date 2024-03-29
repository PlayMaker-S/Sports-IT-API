package PlayMakers.SportsIT.domain;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Builder
@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
public class CompetitionForm{
    private List<Sector> sectors;
    @Builder.Default
    private List<Answer> answers = null;


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
        private boolean checked;
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

}
