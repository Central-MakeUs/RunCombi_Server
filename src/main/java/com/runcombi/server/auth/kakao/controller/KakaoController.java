package com.runcombi.server.auth.kakao.controller;

import com.runcombi.server.auth.kakao.dto.KakaoLoginRequestDto;
import com.runcombi.server.auth.kakao.dto.LoginResponseDTO;
import com.runcombi.server.auth.kakao.service.KakaoLoginService;
import com.runcombi.server.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class KakaoController {
    private final KakaoLoginService kakaoLoginService;
    @PostMapping("/kakao/login")
    public ApiResponse<LoginResponseDTO> kakaoLogin(@RequestBody KakaoLoginRequestDto kakaoLoginRequestDto) {
         LoginResponseDTO loginResponse = kakaoLoginService.kakaoLogin(kakaoLoginRequestDto.getKakaoAccessToken());
         return ApiResponse.onSuccess(loginResponse);
    }
}
