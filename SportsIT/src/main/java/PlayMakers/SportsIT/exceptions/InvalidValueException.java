package PlayMakers.SportsIT.exceptions;

import PlayMakers.SportsIT.exceptions.BusinessException;

public class InvalidValueException extends BusinessException{
    public InvalidValueException(ErrorCode error) {
        super(error.getMessage());
    }
}
