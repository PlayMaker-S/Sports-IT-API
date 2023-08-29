package PlayMakers.SportsIT.utils.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.validation.FieldError;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ErrorResponse {

    private int code;
    private String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<FieldError> errors;

    @Builder
    public ErrorResponse(int code, String message, List<FieldError> errors) {
        this.code = code;
        this.message = message;
        this.errors = errors;
    }

}
