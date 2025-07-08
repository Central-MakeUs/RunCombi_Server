package com.runcombi.server.global.exception;

import com.runcombi.server.global.exception.code.CustomErrorCode;
import com.runcombi.server.global.exception.code.CustomErrorDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CustomException extends RuntimeException{
    private CustomErrorCode code;

    public CustomErrorDto getErrorReason() {
        return this.code.getError();
    }

    public CustomErrorDto getErrorReasonHttpStatus(){
        return this.code.getErrorHttpStatus();
    }
}
