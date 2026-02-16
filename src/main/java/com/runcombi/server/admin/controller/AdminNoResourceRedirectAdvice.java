package com.runcombi.server.admin.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
public class AdminNoResourceRedirectAdvice {

    @ExceptionHandler(NoResourceFoundException.class)
    public Object handleNoResourceFound(
            NoResourceFoundException exception,
            HttpServletRequest request,
            Authentication authentication
    ) throws NoResourceFoundException {
        String requestUri = request.getRequestURI();
        boolean isAuthenticated = authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken);

        // admin 경로로 접근 시 없는 세뷰 경로에 관한 핸들링
        if (requestUri != null && requestUri.startsWith("/admin")) {
            if (isAuthenticated) {
                return new ModelAndView("redirect:/admin/home");
            }
            return new ModelAndView("redirect:/admin/login");
        }

        throw exception;
    }
}
