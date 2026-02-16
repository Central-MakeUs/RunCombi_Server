package com.runcombi.server.domain.member.entity;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "회원 성별 (MALE: 남성, FEMALE: 여성)")
public enum Gender {
    MALE, FEMALE
}
