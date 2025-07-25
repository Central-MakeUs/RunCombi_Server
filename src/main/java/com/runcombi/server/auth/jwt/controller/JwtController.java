package com.runcombi.server.auth.jwt.controller;

import com.runcombi.server.auth.jwt.dto.ResponseTokenDto;
import com.runcombi.server.domain.member.service.MemberService;
import com.runcombi.server.global.exception.CustomException;
import com.runcombi.server.global.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.runcombi.server.global.exception.code.CustomErrorList.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class JwtController {
    private final MemberService memberService;
    @PostMapping("/refresh")
    public ApiResponse<ResponseTokenDto> refresh(HttpServletRequest request) {
        String refreshToken = request.getHeader("RefreshToken");
        if (!StringUtils.hasText(refreshToken)) {
            throw new CustomException(TOKEN_EMPTY);
        }

        if (!refreshToken.startsWith("Bearer ")) {
            throw new CustomException(REFRESH_TOKEN_INVALID);
        }

        ResponseTokenDto responseTokenDto = memberService.regenerateAccessToken(refreshToken.substring(7));
        return ApiResponse.onSuccess(responseTokenDto);
    }
}
