package com.runcombi.server.global.exception.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CustomErrorList implements CustomErrorCode{
    // JWT
    TOKEN_EMPTY(HttpStatus.UNAUTHORIZED, "TOKEN0001", "토큰값이 존재하지 않습니다."),
    ACCESS_TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "TOKEN0002", "유효하지 않은 AccessToken 입니다."),
    ACCESS_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "TOKEN0003", "만료된 AccessToken 입니다."),

    // kakao 로그인
    KAKAO_TOKEN_EMPTY(HttpStatus.UNAUTHORIZED, "KAKAO0001", "토큰값이 존재하지 않습니다."),
    KAKAO_TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "KAKAO0002", "유효하지 않은 카카오 인증 토큰입니다."),
    KAKAO_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "KAKAO0003", "만료된 카카오 인증 토큰입니다."),

    // Member
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER0001", "사용자가 존재하지 않습니다."),

    // Pet
    PET_COUNT_EXCEEDED(HttpStatus.BAD_REQUEST, "PET0001", "최대 반려 동물 수를 초과하였습니다."),
    PET_NOT_MATCH(HttpStatus.BAD_REQUEST, "PET0002", "회원님의 반려 동물이 아닙니다."),
    PET_ID_INVALID(HttpStatus.BAD_REQUEST, "PET0003", "유효하지 않은 반려 동물입니다."),
    PET_COUNT_MINIMUM(HttpStatus.BAD_REQUEST, "PET0004", "최소 반려 동물 수는 1마리 입니다."),

    // Run
    RUN_REQUIRE_PET(HttpStatus.BAD_REQUEST, "RUN0001", "반려 동물이 선택되지 않았습니다."),
    RUN_MEMBER_STYLE_NULL(HttpStatus.BAD_REQUEST, "RUN0002", "회원의 운동 스타일이 선택되지 않았습니다."),
    RUN_ID_INVALID(HttpStatus.BAD_REQUEST, "RUN0003", "유효하지 않은 산책 번호입니다."),

    // S3 이미지 업로드 에러
    S3_IMAGE_UPLOAD_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "BUCKET0001", "이미지 업로드에 실패했습니다."),
    S3_IMAGE_DELETE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "BUCKET0002", "기존 이미지 삭제에 실패했습니다."),

    // 필수 필드 값이 비어있는 경우 에러
    DEFAULT_FIELD_NULL(HttpStatus.BAD_REQUEST, "FIELD0001", "필수 입력 필드가 비어있습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public CustomErrorDto getError() {
        return CustomErrorDto.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .build();
    }

    @Override
    public CustomErrorDto getErrorHttpStatus() {
        return CustomErrorDto.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .httpStatus(httpStatus)
                .build();
    }
}
