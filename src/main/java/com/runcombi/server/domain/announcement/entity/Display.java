package com.runcombi.server.domain.announcement.entity;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "노출 여부 (Y: 노출, N: 미노출)")
public enum Display {
    Y, N
}
