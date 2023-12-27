package PlayMakers.SportsIT.utils.api;

import PlayMakers.SportsIT.competitions.dto.CompetitionDto;

public class CompetitionResponse extends CommonResponse<CompetitionDto>{
    public CompetitionResponse(int code, boolean success, CompetitionDto result) {
        super(code, success, result);
    }
}
