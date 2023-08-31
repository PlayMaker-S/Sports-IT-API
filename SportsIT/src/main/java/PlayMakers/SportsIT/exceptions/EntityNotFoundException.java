package PlayMakers.SportsIT.exceptions;

public class EntityNotFoundException extends BusinessException {
    public EntityNotFoundException(ErrorCode error) {
        super(error);
    }
    public EntityNotFoundException(ErrorCode error, String message) {
        super(error);
    }
}
