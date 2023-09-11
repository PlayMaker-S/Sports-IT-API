package PlayMakers.SportsIT.utils.api;

import PlayMakers.SportsIT.dto.CompetitionDto;
import org.springframework.validation.FieldError;

import java.util.List;

public class ApiUtils {
    public static <T> CommonResponse<T> success(int code, T resource) {
        return new CommonResponse<>(code, true, resource);
    }

    public static CommonResponse<Object> created(int code) {
        return new CommonResponse<>(code, true, null);
    }

    public static <T> CommonResponse<T> created(int code, T resource) {
        return new CommonResponse<>(code, true, resource);
    }
}
