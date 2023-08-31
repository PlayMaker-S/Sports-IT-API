package PlayMakers.SportsIT.exceptions;

import PlayMakers.SportsIT.exceptions.BusinessException;

public class InvalidValueException extends BusinessException{
    public InvalidValueException(ErrorCode error) {
        super(error);
    }
    public InvalidValueException(ErrorCode error, String message) {
        super(error, message);
    }
}
