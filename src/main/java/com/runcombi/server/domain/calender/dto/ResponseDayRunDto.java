package com.runcombi.server.domain.calender.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Schema(description = "일별 산책 목록 항목 DTO")
public class ResponseDayRunDto {
    @Schema(description = "산책 ID", example = "501")
    private Long runId;
    @Schema(description = "산책 시간(분)", example = "54")
    private Integer runTime; // minute 기준
    @Schema(description = "산책 거리(km)", example = "4.92")
    private Double runDistance; // km 기준
    @Schema(description = "산책 대표 이미지 URL", example = "https://runcombi.s3.ap-northeast-2.amazonaws.com/run/501xxx")
    private String runImageUrl; // 산책 이미지
    @Schema(description = "산책 기록 시각", example = "2026-02-16T20:11:00")
    private LocalDateTime regDate; // 등록 일자
}
