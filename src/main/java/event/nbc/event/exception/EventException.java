package event.nbc.event.exception;

import event.nbc.exception.BaseException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class EventException extends BaseException {
    private final EventExceptionCode errorCode;
    private final HttpStatus httpStatus;
    private final String message;

    public EventException(EventExceptionCode errorCode) {
        this.errorCode = errorCode;
        this.httpStatus = errorCode.getHttpStatus();
        this.message = errorCode.getMessage();
    }
}
