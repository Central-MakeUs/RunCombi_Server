package com.runcombi.server.domain.calender.dto;

import com.runcombi.server.domain.pet.entity.RunStyle;
import com.runcombi.server.domain.run.dto.PetCalDto;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class RequestAddRunDto {
    private Integer memberCal;          // 멤버 소모 칼로리
    private RunStyle memberRunStyle;    // RUNNING : 조깅, WALKING : 빠른 걷기, SLOW_WALKING : 걷기
    private Integer runTime;            // minute 기준
    private Double runDistance;         // km 기준
    private LocalDateTime regDate;      // 시작 일시
    private List<PetCalDto> petCalList; // 반려 동물 데이터

    @Override
    public String toString() {
        return "memberCal ::: " + memberCal
                + ", memberRunStyle ::: " + memberRunStyle
                + ", runTime ::: " + runTime
                + ", runDistance ::: " + runDistance
                + ", regDate ::: " + regDate
                + ", petCalList.size() ::: " + petCalList.size();
    }
}