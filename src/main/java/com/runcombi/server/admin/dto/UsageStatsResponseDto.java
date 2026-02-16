package com.runcombi.server.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsageStatsResponseDto {
    private UsageStatsBucketType bucketType;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long totalCount;
    private List<UsageStatsPointDto> series;
}
