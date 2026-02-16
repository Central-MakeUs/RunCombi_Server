package com.runcombi.server.domain.run.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "산책 중간 저장 요청 DTO")
public class RequestMidRunUpdateDto {
    @Schema(description = "산책 ID", example = "501")
    private Long runId;                     // 산책 아이디
    @Schema(description = "산책 시간(분)", example = "32")
    private Integer runTime;                // minute 기준
    @Schema(description = "산책 거리(km)", example = "2.85")
    private Double runDistance;             // km 기준

    @Override
    public String toString() {
        return "runId : " + runId +
                ", runTime : " + runTime +
                ", runDistance : " + runDistance;
    }
}
