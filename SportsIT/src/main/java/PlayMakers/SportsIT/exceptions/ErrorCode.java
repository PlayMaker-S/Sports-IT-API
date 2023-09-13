package PlayMakers.SportsIT.exceptions;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ErrorCode {
    // COMMON
    INVALID_INPUT_VALUE(400, "COMMON-001", " Invalid Input Value"),
    INVALID_TYPE_VALUE(400, "COMMON-002", " Invalid Type Value"),
    UNAUTHORIZED(401, "COMMON-003", " Unauthorized"),
    ACCESS_DENIED(403, "COMMON-004", "Access Denied"),
    ENTITY_NOT_FOUND(404, "COMMON-005", " Entity Not Found"),
    METHOD_NOT_ALLOWED(405, "COMMON-006", " Invalid Input Value"),
    INTERNAL_SERVER_ERROR(500, "COMMON-007", "Server Error"),

    // AUTH
    EMPTY_TOKEN(401, "AUTH-001", "로그인이 필요합니다."),
    EXPIRED_TOKEN(401, "AUTH-002", "다시 로그인 해주세요."),
    NOT_HOST(403, "AUTH-003", "대회 개최자만 접근 가능합니다."),
    NOT_ADMIN(403, "AUTH-004", "관리자만 접근 가능합니다."),
    NOT_PLAYER(403, "AUTH-005", "체육인만 접근 가능합니다."),

    // USER
    INVALID_INPUT_USERNAME(400, "USER-001", "닉네임을 3자 이상 입력하세요"),
    NOT_EQUAL_INPUT_PASSWORD(400, "USER-002", "비밀번호가 일치하지 않습니다"),
    INVALID_USERNAME(400, "USER-003", "알파벳 대소문자와 숫자로만 입력하세요"),
    INVALID_PASSWORD(400, "USER-004", "비밀번호를 4자 이상 입력하세요"),
    DUPLICATED_USERNAME(400, "USER-005", "이미 등록된 아이디입니다."),
    INVALID_LOGIN_INPUT(400, "USER-006", "이메일 또는 비밀번호를 정확히 입력해주세요."),
    USER_NOT_FOUND(404, "USER-007", "해당 이름의 유저가 존재하지 않습니다."),

    // COMPETITION
    INVALID_COMPETITION_PARAMETER(400, "COMPETITION-001", "대회 정보를 정확히 입력해주세요."),
    CONTENT_NOT_COMPLETE(400, "COMPETITION-002", "대회 내용을 모두 입력해주세요."),
    COMPETITION_NOT_FOUND(404, "COMPETITION-003", "해당 대회가 존재하지 않습니다."),
    CONVERTING_FAILED(400, "COMPETITION-004", "파일 변환에 실패했습니다."),
    COMPETITION_NOT_AVAILABLE(400, "COMPETITION-005", "대회 참가가 불가능합니다."),
    ;

    private final int status;
    private final String message;
    private final String code;

    ErrorCode(final int status, final String code, final String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

    public int getStatus() {
        return status;
    }
    public String getCode() {
        return code;
    }
    public String getMessage() {
        return this.message;
    }
}
