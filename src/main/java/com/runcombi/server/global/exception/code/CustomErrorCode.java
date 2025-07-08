package com.runcombi.server.global.exception.code;

public interface CustomErrorCode {
    public CustomErrorDto getError();

    public CustomErrorDto getErrorHttpStatus();
}
