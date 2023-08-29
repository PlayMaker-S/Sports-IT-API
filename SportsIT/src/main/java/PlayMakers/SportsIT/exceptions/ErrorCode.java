package PlayMakers.SportsIT.exceptions;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ErrorCode {
    // Common
    INVALID_INPUT_VALUE(400, " Invalid Input Value"),
    INVALID_TYPE_VALUE(400, " Invalid Type Value"),
    UNAUTHORIZED(401, " Unauthorized"),
    ACCESS_DENIED(403, "Access Denied"),
    ENTITY_NOT_FOUND(404,  " Entity Not Found"),
    METHOD_NOT_ALLOWED(405,  " Invalid Input Value"),
    INTERNAL_SERVER_ERROR(500, "Server Error"),


    // 유저
    INVALID_INPUT_USERNAME(400, "닉네임을 3자 이상 입력하세요"),
    NOT_EQUAL_INPUT_PASSWORD(400,  "비밀번호가 일치하지 않습니다"),
    INVALID_USERNAME(400,  "알파벳 대소문자와 숫자로만 입력하세요"),
    INVALID_PASSWORD(400,  "비밀번호를 4자 이상 입력하세요"),
    DUPLICATED_USERNAME(400, "이미 등록된 아이디입니다."),
    LOGIN_INPUT_INVALID(400, "로그인 정보를 다시 확인해주세요."),
    EMPTY_TOKEN(401, "로그인이 필요합니다."),
    EXPIRED_TOKEN(401, "다시 로그인 해주세요."),
    NOT_HOST(403, "대회 개최자만 접근 가능합니다."),
    NOT_ADMIN(403, "관리자만 접근 가능합니다."),
    USER_NOT_FOUND(404,  "해당 이름의 유저가 존재하지 않습니다."),

    // 대회
    RECRUITING_ENDS_BEFORE_STARTS(400, "모집 마감일이 모집 시작일보다 빠릅니다."),
    COMPETITION_STARTS_BEFORE_RECRUITING_ENDS(400, "대회 시작일이 모집 마감일보다 빠릅니다."),
    COMPETITION_ENDS_BEFORE_STARTS(400, "대회 종료일이 대회 시작일보다 빠릅니다."),
    COMPETITION_ALREADY_STARTED(400, "대회가 이미 시작되었습니다."),
    COMPETITION_ALREADY_CLOSED(400, "대회가 이미 종료되었습니다."),
    CONTENT_NOT_COMPLETE(400, "대회 내용을 모두 입력해주세요."),
    COMPETITION_NOT_FOUND(404, "해당 대회가 존재하지 않습니다."),
    CONVERTING_FAILED(400, "파일 변환에 실패했습니다."),
    ;

    private final String message;
    private final int status;

    ErrorCode(final int status, final String message) {
        this.status = status;
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
    public int getStatus() {
        return status;
    }
}
