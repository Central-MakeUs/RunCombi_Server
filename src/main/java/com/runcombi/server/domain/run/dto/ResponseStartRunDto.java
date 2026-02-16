package com.runcombi.server.domain.run.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "산책 시작 응답 DTO")
public class ResponseStartRunDto {
    @Schema(description = "생성된 산책 ID", example = "501")
    private Long runId;
    @Schema(description = "회원의 최초 산책 여부(Y/N)", example = "N")
    private String isFirstRun;
    @Schema(description = "이번 달 n번째 산책 순번", example = "7")
    private int nthRun;
}
