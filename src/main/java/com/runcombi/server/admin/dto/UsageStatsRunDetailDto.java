package com.runcombi.server.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsageStatsRunDetailDto {
    private Long runId;
    private LocalDateTime regDate;
    private Integer runTime;
    private Double runDistance;
    private Integer memberCal;
    private String memberRunStyle;
    private String runEvaluating;
    private String memo;
    private String runImageUrl;
    private String routeImageUrl;
    private List<UsageStatsRunPetDto> pets;
}
