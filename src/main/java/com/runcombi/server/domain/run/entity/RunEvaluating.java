package com.runcombi.server.domain.run.entity;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "산책 난이도 평가")
public enum RunEvaluating {
    SO_EASY,    // 쏘이지
    EASY,       // 이지
    NORMAL,     // 보통
    HARD,       // 숨참
    VERY_HARD   // 힘듦
}
