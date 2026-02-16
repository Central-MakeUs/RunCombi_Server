package com.runcombi.server.domain.calender.dto;

import com.runcombi.server.domain.pet.entity.RunStyle;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Schema(description = "산책 상세 수정 요청 DTO")
public class RequestUpdateRunDetailDto {
    @Schema(description = "수정 대상 산책 ID", example = "501")
    private Long runId;                 // 산책 번호
    @Schema(description = "수정할 산책 시작 시각", example = "2026-02-15T19:30:00")
    private LocalDateTime regDate;      // 시작 일시
    @Schema(description = "수정할 회원 산책 스타일", example = "RUNNING")
    private RunStyle memberRunStyle;    // RUNNING : 조깅, WALKING : 빠른 걷기, SLOW_WALKING : 걷기
    @Schema(description = "수정할 산책 시간(분)", example = "52")
    private Integer runTime;            // minute 기준
    @Schema(description = "수정할 산책 거리(km)", example = "4.20")
    private Double runDistance;         // km 기준
}
