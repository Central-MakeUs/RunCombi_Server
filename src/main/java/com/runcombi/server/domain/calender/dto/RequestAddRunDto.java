package com.runcombi.server.domain.calender.dto;

import com.runcombi.server.domain.pet.entity.RunStyle;
import com.runcombi.server.domain.run.dto.PetCalDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Schema(description = "산책 수동 등록 요청 DTO")
public class RequestAddRunDto {
    @Schema(description = "회원 산책 스타일", example = "WALKING")
    private RunStyle memberRunStyle;    // RUNNING : 조깅, WALKING : 빠른 걷기, SLOW_WALKING : 걷기
    @Schema(description = "산책 시간(분)", example = "47")
    private Integer runTime;            // minute 기준
    @Schema(description = "산책 거리(km)", example = "3.75")
    private Double runDistance;         // km 기준
    @Schema(description = "산책 시작 시각", example = "2026-02-15T19:30:00")
    private LocalDateTime regDate;      // 시작 일시
    @Schema(description = "참여 반려동물 목록")
    private List<PetCalDto> petCalList; // 반려 동물 데이터

    @Override
    public String toString() {
        return " memberRunStyle ::: " + memberRunStyle
                + ", runTime ::: " + runTime
                + ", runDistance ::: " + runDistance
                + ", regDate ::: " + regDate
                + ", petCalList.size() ::: " + petCalList.size();
    }
}
