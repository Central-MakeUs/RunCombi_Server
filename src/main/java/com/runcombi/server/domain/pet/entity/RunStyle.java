package com.runcombi.server.domain.pet.entity;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "산책 스타일")
public enum RunStyle {
    RUNNING,      // 에너지가 넘쳐요!, 조깅
    WALKING,      // 여유롭게 걸어요, 빠른 걷기
    SLOW_WALKING  // 천천히 걸으며 자주 쉬어요, 걷기
}
