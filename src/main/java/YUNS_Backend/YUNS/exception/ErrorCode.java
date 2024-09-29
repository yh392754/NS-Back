package YUNS_Backend.YUNS.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;
@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    //에러 코드
    CUSTOM_ERROR(BAD_REQUEST, "SA1", "잘못된 요청입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
