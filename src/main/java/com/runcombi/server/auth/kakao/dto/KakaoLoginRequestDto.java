package com.runcombi.server.auth.kakao.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "카카오 로그인 요청 DTO")
public class KakaoLoginRequestDto {
    @Schema(description = "카카오 OAuth Access Token", example = "kakao_access_token_value")
    private String kakaoAccessToken;
}
