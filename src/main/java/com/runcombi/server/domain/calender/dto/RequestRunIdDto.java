package com.runcombi.server.domain.calender.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "산책 ID 요청 DTO")
public class RequestRunIdDto {
    @Schema(description = "산책 ID", example = "501")
    private Long runId;
}
