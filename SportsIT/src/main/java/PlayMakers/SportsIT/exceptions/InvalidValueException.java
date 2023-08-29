package PlayMakers.SportsIT.exceptions;

import PlayMakers.SportsIT.exceptions.BusinessException;

public class InvalidValueException extends BusinessException{
    public InvalidValueException(String msg) {
        super(msg);
    }
}
