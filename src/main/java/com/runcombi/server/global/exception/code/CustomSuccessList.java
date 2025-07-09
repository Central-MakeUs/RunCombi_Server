package com.runcombi.server.global.exception.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CustomSuccessList implements CustomSuccessCode{

    // 일반 성공 응답
    _OK(HttpStatus.OK, "STATUS200", "요청에 성공하셨습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public CustomSuccessDto getSuccess() {
        return CustomSuccessDto.builder()
                .message(message)
                .code(code)
                .isSuccess(true)
                .build();
    }

    @Override
    public CustomSuccessDto getSuccessHttpStatus() {
        return CustomSuccessDto.builder()
                .message(message)
                .code(code)
                .isSuccess(true)
                .httpStatus(httpStatus)
                .build();
    }
}
