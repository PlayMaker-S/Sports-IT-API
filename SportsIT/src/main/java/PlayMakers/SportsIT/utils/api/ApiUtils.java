package PlayMakers.SportsIT.utils.api;

import PlayMakers.SportsIT.dto.CompetitionDto;
import org.springframework.validation.FieldError;

import java.util.List;

public class ApiUtils {
    public static <T> CommonResponse success(int code, T resource) {
        if (resource.equals(CompetitionDto.Form.class)) return new CompetitionResponse(code, true, (CompetitionDto) resource);
        return new CommonResponse<>(code, true, resource);
    }
    public static ErrorResponse fail(int code, String message, List<FieldError> errors) {
        return new ErrorResponse(code, message, errors);
    }
}
