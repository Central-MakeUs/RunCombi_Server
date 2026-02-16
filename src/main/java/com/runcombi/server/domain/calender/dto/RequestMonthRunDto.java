package com.runcombi.server.domain.calender.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "월별 날짜-산책ID 매핑 DTO")
public class RequestMonthRunDto {
    @Schema(description = "날짜(yyyyMMdd)", example = "20260216")
    private String date;           // "yyyyMMdd"
    @Schema(description = "해당 날짜의 산책 ID 목록(없으면 null)", example = "[501, 502]")
    private List<Long> runId;
}
