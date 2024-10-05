package YUNS_Backend.YUNS.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;
@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // 노트북 관련 에러 //
    NOTEBOOK_NOT_FOUND(NOT_FOUND, "NE1", "해당하는 notebookId가 존재하지 않습니다."),

    // 사용자 관련 에러 //
    USER_EMAIL_INVALID(BAD_REQUEST, "UE1", "잘못된 이메일 형식입니다."),
    USER_PASSWORD_INVALID(BAD_REQUEST, "UE2", "잘못된 비밀번호 형식입니다"),
    USER_INVALID_INPUT(BAD_REQUEST, "UE3", "잘못된 입력입니다."),
    USER_ALREADY_EXIST(BAD_REQUEST, "UE4", "가입된 사용자입니다."),
    USER_REGIST_FAILED(INTERNAL_SERVER_ERROR, "UE5", "회원가입에 실패했습니다."),
    USER_NOT_FOUND(BAD_REQUEST, "UE6", "해당하는 사용자가 없습니다."),

    // 권한 관련 에러 //
    NO_PERMISSION(FORBIDDEN, "AE1", "해당 요청에 대한 권한이 없습니다."),

    // 대여 관련 에러 //
    ALREADY_RENTAL(CONFLICT, "RE1", "이미 대여중인 노트북이 있습니다."),
    ALREADY_RENTAL_REQUEST(CONFLICT, "RE2", "이미 대여 신청한 노트북이 있습니다."),
    RENTAL_NOT_FOUND_BY_USER(NOT_FOUND, "RE3", "대여중인 노트북이 없습니다."),
    DIFFERENT_NOTEBOOK(BAD_REQUEST, "RE4", "해당 사용자가 대여중인 노트북이 아닙니다."),

    RENTAL_NOT_FOUND(NOT_FOUND, "RE5", "해당 대여 기록을 찾을 수 없습니다.");


    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
