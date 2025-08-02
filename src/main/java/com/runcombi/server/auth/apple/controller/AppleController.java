package com.runcombi.server.auth.apple.controller;

import com.runcombi.server.auth.apple.dto.AppleLoginRequestDto;
import com.runcombi.server.auth.apple.dto.AppleTokenResponseDto;
import com.runcombi.server.auth.apple.service.AppleLoginService;
import com.runcombi.server.auth.kakao.dto.LoginResponseDTO;
import com.runcombi.server.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AppleController {
    private final AppleLoginService appleLoginService;
    @PostMapping("/apple/login")
    public ApiResponse<LoginResponseDTO> appleLogin(@RequestBody AppleLoginRequestDto appleLoginRequestDto) {
        AppleTokenResponseDto appleTokenResponseDto = appleLoginService.requestTokenToApple(appleLoginRequestDto.getAuthorizationCode());
        Map<String, Object> userInfo = appleLoginService.parseIdToken(appleTokenResponseDto.getIdToken());
        LoginResponseDTO loginResponseDTO = appleLoginService.appleLogin((String) userInfo.get("sub"), (String) userInfo.get("email"), appleTokenResponseDto.getRefreshToken());

        return ApiResponse.onSuccess(loginResponseDTO);
    }
}
