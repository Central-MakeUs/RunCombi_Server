package com.runcombi.server.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsageStatsRunPetDto {
    private Long petId;
    private String petName;
    private Integer petCal;
}
