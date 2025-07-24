package com.runcombi.server.domain.calender.dto;

import com.runcombi.server.domain.run.entity.RunEvaluating;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ResponseRunDto {
    private Long runId;
    private Integer runTime; // minute 기준
    private Double runDistance; // km 기준
    private RunEvaluating runEvaluating; // 달리기 평가 (SO_EASY, EASY, NORMAL, HARD, VERY_HARD)
    private String runImageUrl; // 산책 이미지
    private String routeImageUrl; // 산책 경로 이미지
    private String memo; // 메모
    private LocalDateTime regDate; // 등록 일자
    List<ResponseRunPetDto> petData; // 반려 동물 데이터
}
