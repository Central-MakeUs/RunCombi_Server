package com.runcombi.server.domain.calender.dto;

import com.runcombi.server.domain.pet.entity.RunStyle;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class RequestUpdateRunDetailDto {
    private Long runId;                 // 산책 번호
    private LocalDateTime regDate;      // 시작 일시
    private RunStyle memberRunStyle;    // RUNNING : 조깅, WALKING : 빠른 걷기, SLOW_WALKING : 걷기
    private Integer runTime;            // minute 기준
    private Double runDistance;         // km 기준
}
