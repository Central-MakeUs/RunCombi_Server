package com.runcombi.server.global.exception;

import com.runcombi.server.global.exception.code.CustomErrorCode;

public class ExceptionHandler extends CustomException{
    public ExceptionHandler(CustomErrorCode code) {
        super(code);
    }
}
