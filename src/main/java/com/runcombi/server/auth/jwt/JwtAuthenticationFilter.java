package com.runcombi.server.auth.jwt;

import io.micrometer.common.util.StringUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@AllArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final AuthenticationManager authenticationManager;
    @Autowired
    private final JwtService jwtService; // JWT 토큰 처리 서비스

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // /admin/** 경로에 대해서는 인증 건너뛰기
        String path = request.getServletPath();
        if (path.startsWith("/admin")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = getTokenFromRequest(request);

        if (StringUtils.isNotEmpty(token)) {
            String jwtStatus = jwtService.validateToken(token);
            if (jwtStatus.equals("live")) {
                Authentication authentication = jwtService.getAuthentication(token);

                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else if(jwtStatus.equals("expired")) {
                handleUnauthorizedResponse(response, "TOKEN0003","만료된 AccessToken 입니다.");
                return;
            } else {
                handleUnauthorizedResponse(response, "TOKEN0002","유효하지 않은 AccessToken 입니다.");
                return;
            }
        }else {
            handleUnauthorizedResponse(response, "TOKEN0001", "토큰이 존재하지 않습니다.");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private void handleUnauthorizedResponse(HttpServletResponse response, String code, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"isSuccess\": false, \"code\": \"" + code + "\", \"message\": \"" + message + "\"}");
    }
}
