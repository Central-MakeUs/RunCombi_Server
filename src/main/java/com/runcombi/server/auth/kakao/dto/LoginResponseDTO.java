package com.runcombi.server.auth.kakao.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Schema(description = "소셜 로그인 성공 응답 DTO")
public class LoginResponseDTO {
    @Schema(description = "회원 ID", example = "15")
    private Long memberId;
    @Schema(description = "회원 이메일", example = "user@runcombi.com")
    private String email;
    @Schema(description = "서버 발급 Access Token", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String accessToken;
    @Schema(description = "서버 발급 Refresh Token", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String refreshToken;
    @Schema(description = "회원가입 추가 정보 입력 완료 여부 (Y/N)", example = "N")
    private String finishRegister; // 정보 값까지 등록된 회원이면 Y, 아니면 N
}
