package PlayMakers.SportsIT.utils.api;

public class PostResponse<T> extends CommonResponse<T>{
    private final int code = 201;
    public PostResponse(int code, boolean success, T result) {
        super(code, success, null);
    }
}
