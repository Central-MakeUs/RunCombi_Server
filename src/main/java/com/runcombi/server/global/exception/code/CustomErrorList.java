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
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER0001", "사용자가 존재하지 않습니다.");

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
