package PlayMakers.SportsIT.exceptions;

public class RequestDeniedException extends BusinessException{
    public RequestDeniedException(ErrorCode error) {
        super(error);
}
    public RequestDeniedException(ErrorCode error, String message) {
        super(error, message);
    }
}
