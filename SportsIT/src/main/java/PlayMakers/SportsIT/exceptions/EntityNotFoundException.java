package PlayMakers.SportsIT.exceptions;

public class EntityNotFoundException extends BusinessException {
    public EntityNotFoundException(ErrorCode error) {
        super(error.getMessage());
    }
}
