package PlayMakers.SportsIT.exceptions;

import PlayMakers.SportsIT.exceptions.BusinessException;

public class UnAuthorizedException extends BusinessException {
    public UnAuthorizedException(ErrorCode error) {
        super(error);
    }
    public UnAuthorizedException(ErrorCode error, String message) {
        super(error, message);
    }

}
