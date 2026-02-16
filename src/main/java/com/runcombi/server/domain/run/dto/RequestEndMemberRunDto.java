package com.runcombi.server.domain.run.dto;

import com.runcombi.server.domain.run.entity.RunEvaluating;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "산책 종료 시 회원 산책 데이터 DTO")
public class RequestEndMemberRunDto {
    @Schema(description = "산책 ID", example = "501")
    private Long runId;                     // 산책 아이디
    @Schema(description = "최종 산책 시간(분)", example = "54")
    private Integer runTime;                // minute 기준
    @Schema(description = "최종 산책 거리(km)", example = "4.92")
    private Double runDistance;             // km 기준

    @Override
    public String toString() {
        return "runId : " + runId +
                ", runTime : " + runTime +
                ", runDistance : " + runDistance;
    }
}
