package PlayMakers.SportsIT.utils.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

@Getter
public class CommonResponse<T> {
    private final int code;
    private final boolean success;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final T result;

    @Builder
    public CommonResponse(int code, boolean success, T result) {
        this.code = code;
        this.success = success;
        this.result = result;
    }
}
