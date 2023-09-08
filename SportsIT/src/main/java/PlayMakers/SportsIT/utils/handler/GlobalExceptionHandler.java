package PlayMakers.SportsIT.utils.handler;

import PlayMakers.SportsIT.exceptions.*;
import PlayMakers.SportsIT.utils.api.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    private final HttpStatus HTTP_STATUS_OK = HttpStatus.OK;

    /**
     * BadCredentialsException 핸들링
     * @param e BadCredentialsException
     * @return ResponseEntity<ErrorResponse>
     */
    @ExceptionHandler(BadCredentialsException.class)
    protected final ResponseEntity<ErrorResponse> handleBadCredentialsException(BadCredentialsException e) {
        log.error("BadCredentialsException", e);
        final ErrorResponse response = ErrorResponse.of(ErrorCode.INVALID_LOGIN_INPUT, e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getCode()));
    }

    /**
     * UnAuthorizedException 핸들링
     *
     * @param e UnAuthorizedException
     * @return ResponseEntity<ErrorResponse>
     */
    @ExceptionHandler(UnAuthorizedException.class)
    protected final ResponseEntity<ErrorResponse> handleUnAuthorizedException(UnAuthorizedException e) {
        log.error("UnAuthorizedException", e);
        final ErrorResponse response = ErrorResponse.of(e.getErrorCode(), e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getCode()));
    }

    /**
     * EntityNotFoundException 핸들링
     *
     * @param e EntityNotFoundException
     * @return ResponseEntity<ErrorResponse>
     */
    @ExceptionHandler(EntityNotFoundException.class)
    protected final ResponseEntity<ErrorResponse> handleEntityNotFoundException(EntityNotFoundException e) {
        log.info("EntityNotFoundException", e);
        final ErrorResponse response = ErrorResponse.of(e.getErrorCode(), e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getCode()));
    }

    /**
     * RequestDeniedException 핸들링
     * @param e RequestDeniedException
     * @return ResponseEntity<ErrorResponse>
     */
    @ExceptionHandler(RequestDeniedException.class)
    protected final ResponseEntity<ErrorResponse> handleRequestDeniedException(RequestDeniedException e) {
        log.info("RequestDeniedException", e);
        final ErrorResponse response = ErrorResponse.of(e.getErrorCode(), e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getCode()));
    }

    /**
     * InvalidValueException 핸들링
     * @param e InvalidValueException
     * @return ResponseEntity<ErrorResponse>
     */
    @ExceptionHandler(InvalidValueException.class)
    protected final ResponseEntity<ErrorResponse> handleInvalidValueException(InvalidValueException e) {
        log.error("InvalidValueException", e);
        final ErrorResponse response = ErrorResponse.of(e.getErrorCode(), e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getCode()));
    }

    /**
     * 모든 Exception 핸들링
     *
     * @param e Exception
     * @return ResponseEntity<ErrorResponse>
     */
    @ExceptionHandler(Exception.class)
    protected final ResponseEntity<ErrorResponse> handleAllExceptions(Exception e) {
        log.error("Exception", e);
        final ErrorResponse response = ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(response, HTTP_STATUS_OK);
    }
}
