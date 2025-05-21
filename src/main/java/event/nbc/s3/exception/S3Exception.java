package event.nbc.s3.exception;

import org.springframework.http.HttpStatus;

import event.nbc.exception.BaseException;
import lombok.Getter;

@Getter
public class S3Exception extends BaseException {

	private final S3ExceptionCode errorCode;
	private final HttpStatus httpStatus;
	private final String message;

	public S3Exception(S3ExceptionCode errorCode) {
		this.errorCode = errorCode;
		this.httpStatus = errorCode.getHttpStatus();
		this.message = errorCode.getMessage();
	}
}
