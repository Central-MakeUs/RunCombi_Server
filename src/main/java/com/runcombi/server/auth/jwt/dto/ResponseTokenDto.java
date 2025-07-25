package com.runcombi.server.auth.jwt.dto;

import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ResponseTokenDto {
    private String refreshToken;
    private String accessToken;
}
