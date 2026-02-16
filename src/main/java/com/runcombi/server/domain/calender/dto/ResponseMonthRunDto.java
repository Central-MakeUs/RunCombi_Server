package com.runcombi.server.domain.calender.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Schema(description = "월별 산책 데이터 응답 DTO")
public class ResponseMonthRunDto {
    @Schema(description = "한 달 날짜별 산책 ID 데이터")
    private List<RequestMonthRunDto>  monthData;   // 한 달 산책 데이터
    @Schema(description = "월 평균 산책 시간(분)", example = "43")
    private Integer avgTime;
    @Schema(description = "월 평균 소모 칼로리(kcal)", example = "233")
    private Integer avgCal;                 // 평균 칼로리
    @Schema(description = "월 평균 산책 거리(km)", example = "3.41")
    private Double avgDistance;             // 평균 산책 거리
    @Schema(description = "월 최다 산책 유형", example = "WALKING")
    private String mostRunStyle;            // 한 달 가장 많은 산책 유형
}
