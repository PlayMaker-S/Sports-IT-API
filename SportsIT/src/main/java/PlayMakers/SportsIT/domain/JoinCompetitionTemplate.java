package PlayMakers.SportsIT.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
public class JoinCompetitionTemplate {
    private List<Sector> sectors;

    private List<Questionnaire> questionnaires = null;

    @Data
    @AllArgsConstructor @NoArgsConstructor
    public static class Sector{
        private String title;
        private Long cost;
        private Long expandCost;
        private boolean multi;
        private List<SubSector> subSectors;

    }

    @Data
    @AllArgsConstructor @NoArgsConstructor
    public static class SubSector{
        private String name;
    }

    @Data
    @AllArgsConstructor @NoArgsConstructor
    public static class Questionnaire{
        private String question;
        private Type type;
        private List<Opt> options;
    }
    @Data
    @AllArgsConstructor @NoArgsConstructor
    public static class Opt{
        private String opt;
    }

    public enum Type{
        SINGLE,
        MULTI,
        SHORT,
        LONG,
    }

}
