package com.runcombi.server.auth.apple.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "Apple 로그인 요청 DTO")
public class AppleLoginRequestDto {
    @Schema(description = "Apple Authorization Code", example = "c123abc456def")
    private String authorizationCode;
}
