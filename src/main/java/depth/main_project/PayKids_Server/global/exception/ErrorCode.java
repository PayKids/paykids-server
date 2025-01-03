package depth.main_project.PayKids_Server.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    //Common
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버에 오류가 발생하였습니다."),
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "Request Body를 통해 전달된 값이 유효하지 않습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "User not found"),
    STAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "Stage not found"),
    QUIZ_NOT_FOUND(HttpStatus.NOT_FOUND, "Quiz not found"),
    TOKEN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "토큰 해석 문제가 발생했습니다"),
    TOKEN_EXPIRED_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "토큰 유효기간이 만료되었습니다");


    private final HttpStatus httpStatus;
    private final String message;
}
