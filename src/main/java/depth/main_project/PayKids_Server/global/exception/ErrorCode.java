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
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "Category not found"),
    ALLOWANCE_NOT_FOUND(HttpStatus.NOT_FOUND, "Allowance not found"),
    NO_ALLOWANCE_FOUND(HttpStatus.NOT_FOUND, "내역이 없습니다."),
    CATEGORY_ERROR(HttpStatus.BAD_REQUEST, "기타 항목은 지울수 없습니다."),
    FILE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "S3 파일 업로드 실패"),
    FILE_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "S3 파일 삭제 실패"),
    PREVIOUS_IMAGE_NOT_FOUND( HttpStatus.BAD_REQUEST, "Previous image not found"),
    STAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "Stage not found"),
    TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "ID Token is invalid"),
    NICKNAME_ALREADY_EXISTS(HttpStatus.CONFLICT,"Nickname already exists"),
    SAME_NICKNAME(HttpStatus.BAD_REQUEST, "새 닉네임이 다른 유저의 닉네임과 겹치거나 이전 닉네임과 같습니다."),
    NICKNAME_TOO_LONG(HttpStatus.BAD_REQUEST, "Nickname의 글자수는 최대 8글자"),
    INVALID_NICKNAME(HttpStatus.BAD_REQUEST, "Nickname is invalid(닉네임이 공백인 경우)"),
    QUIZ_NOT_FOUND(HttpStatus.NOT_FOUND, "Quiz not found"),
    GPT_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "하루 최대 이용량 초과 되었습니다"),
    GPT_RESET_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "유저 사용량 초기화 중 에러 발생"),
    TOKEN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "토큰 해석 문제가 발생했습니다"),
    INVALUE_SECRET_KEY(HttpStatus.FORBIDDEN, "유효한 Secret Key가 아닙니다."),
    TOKEN_EXPIRED_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "토큰 유효기간이 만료되었습니다");


    private final HttpStatus httpStatus;
    private final String message;
}
