package com.runcombi.server.auth.kakao.dto;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class LoginResponseDTO {
    private Long memberId;
    private String email;
    private String accessToken;
    private String refreshToken;
    private String finishRegister; // 정보 값까지 등록된 회원이면 Y, 아니면 N
}
