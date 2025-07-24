package com.runcombi.server.domain.calender.dto;

import com.runcombi.server.domain.run.entity.RunEvaluating;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RequestRunEvaluatingDto {
    private Long runId;
    private RunEvaluating runEvaluating;
}
