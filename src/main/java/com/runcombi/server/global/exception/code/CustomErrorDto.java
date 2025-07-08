package com.runcombi.server.global.exception.code;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@Builder
public class CustomErrorDto {
    private HttpStatus httpStatus;

    private final boolean isSuccess;
    private final String code;
    private final String message;
}
