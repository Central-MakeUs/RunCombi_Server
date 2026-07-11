package com.runcombi.server.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsageStatsMemberSummaryDto {
    private Long memberId;
    private String email;
    private String nickname;
    private String provider;
    private String profileImgUrl;
    private String memberStatus;
    private Long runCount;
    private Integer totalRunTime;
    private Double totalRunDistance;
    private LocalDateTime lastRunDate;
}
