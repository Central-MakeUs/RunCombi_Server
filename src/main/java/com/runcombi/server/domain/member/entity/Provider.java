package com.runcombi.server.domain.member.entity;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "소셜 로그인 제공자")
public enum Provider {
    KAKAO, APPLE
}
