package com.runcombi.server.domain.calender.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "날짜 조회 요청 DTO")
public class RequestDateDto {
    @Schema(description = "연도", example = "2026")
    private int year;
    @Schema(description = "월(1~12)", example = "2")
    private int month;
    @Schema(description = "일(일별 조회 시 사용)", example = "16")
    private int day;
}
