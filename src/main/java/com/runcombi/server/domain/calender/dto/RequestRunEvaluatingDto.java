package com.runcombi.server.domain.calender.dto;

import com.runcombi.server.domain.run.entity.RunEvaluating;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "산책 평가 등록 요청 DTO")
public class RequestRunEvaluatingDto {
    @Schema(description = "산책 ID", example = "501")
    private Long runId;
    @Schema(description = "산책 평가", example = "NORMAL")
    private RunEvaluating runEvaluating;
}
