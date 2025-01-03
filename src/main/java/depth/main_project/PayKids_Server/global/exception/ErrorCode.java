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
    NICKNAME_ALREADY_EXISTS(HttpStatus.CONFLICT,"Nickname already exists"),
    SAME_NICKNAME(HttpStatus.BAD_REQUEST, "New nickname cannot be the same as the current nickname"),
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT,"Email already exists"),
    SAME_EMAIL(HttpStatus.BAD_REQUEST, "New email cannot be the same as the current email"),
    FILE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "S3 파일 업로드 실패"),
    FILE_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "S3 파일 삭제 실패"),
    PREVIOUS_IMAGE_NOT_FOUND( HttpStatus.BAD_REQUEST, "Previous image not found"),
    STAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "Stage not found"),
    QUIZ_NOT_FOUND(HttpStatus.NOT_FOUND, "Quiz not found"),
    TOKEN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "토큰 해석 문제가 발생했습니다"),
    TOKEN_EXPIRED_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "토큰 유효기간이 만료되었습니다");


    private final HttpStatus httpStatus;
    private final String message;
}
