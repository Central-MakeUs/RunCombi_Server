package com.runcombi.server.domain.run.dto;

import com.runcombi.server.domain.run.entity.RunEvaluating;
import lombok.Getter;

@Getter
public class RequestEndMemberRunDto {
    private Long runId;                     // 산책 아이디
    private Integer runTime;                // minute 기준
    private Double runDistance;             // km 기준

    @Override
    public String toString() {
        return "runId : " + runId +
                ", runTime : " + runTime +
                ", runDistance : " + runDistance;
    }
}

