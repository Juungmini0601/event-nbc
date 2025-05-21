package event.nbc.s3.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum S3ExceptionCode {

	PRESIGNED_PUT_URL_GENERATION_FAILED(false, HttpStatus.SERVICE_UNAVAILABLE, "이미지 저장에 실패하였습니다."),
	PRESIGNED_GET_URL_GENERATION_FAILED(false, HttpStatus.SERVICE_UNAVAILABLE, "이미지 조회에 실패하였습니다.");

	private final boolean isSuccess;
	private final HttpStatus httpStatus;
	private final String message;
}