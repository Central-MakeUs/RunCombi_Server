package com.runcombi.server.domain.calender.dto;

import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ResponseMonthRunDto {
    private List<MonthRunDto>  monthData;   // 한 달 산책 데이터
    private Integer avgCal;                 // 평균 칼로리
    private Double avgDistance;             // 평균 산책 거리
    private String mostRunStyle;            // 한 달 가장 많은 산책 유형
}
