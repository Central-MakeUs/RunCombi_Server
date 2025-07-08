package com.runcombi.server.global.exception;

import com.runcombi.server.global.exception.code.CustomErrorDto;
import com.runcombi.server.global.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(CustomException.class)
    public ResponseEntity handleCustomException(CustomException customException, HttpServletRequest request) {
        CustomErrorDto errorReasonHttpStatus = customException.getErrorReasonHttpStatus();
        return handleExceptionInternal(customException,errorReasonHttpStatus,null,request);
    }

    private ResponseEntity<Object> handleExceptionInternal(Exception e, CustomErrorDto reason,
                                                           HttpHeaders headers, HttpServletRequest request) {

        ApiResponse<Object> body = ApiResponse.onFailure(reason.getCode(),reason.getMessage(),null);

        WebRequest webRequest = new ServletWebRequest(request);
        return super.handleExceptionInternal(
                e,
                body,
                headers,
                reason.getHttpStatus(),
                webRequest
        );
    }
}
