package com.runcombi.server.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsageStatsPointDto {
    private String label;
    private Long count;
    private LocalDate bucketStartDate;
    private LocalDate bucketEndDate;
}
