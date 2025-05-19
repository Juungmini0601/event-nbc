package event.nbc.domain.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum EventExceptionCode {

    NOT_FOUND_EVENT(false, HttpStatus.NOT_FOUND, "해당 이벤트를 찾을 수 없습니다"),
    EVENT_OUT_OF_STOCK(false, HttpStatus.GONE, "당첨자가 모두 나왔습니다. 다음 기회에 다시 참여해주세요"),
    INVALID_EVENT(false, HttpStatus.BAD_REQUEST, "지금은 이벤트에 참여할 수 없어요");

    private final boolean isSuccess;
    private final HttpStatus httpStatus;
    private final String message;
}
