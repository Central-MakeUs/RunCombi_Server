package com.runcombi.server.domain.calender.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ResponseDayRunDto {
    private Long runId;
    private Integer runTime; // minute 기준
    private Double runDistance; // km 기준
    private String runImageUrl; // 산책 이미지
    private LocalDateTime regDate; // 등록 일자
}
