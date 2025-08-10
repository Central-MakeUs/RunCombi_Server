package com.runcombi.server.domain.announcement.dto;

import com.runcombi.server.domain.announcement.entity.AnnouncementType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Builder
@Getter
public class ResponseAnnouncementDetailDto {
    private Long announcementId;
    private AnnouncementType announcementType;
    private String title;
    private String content;
    private String announcementImageUrl;
    private String code;
    private String eventApplyUrl;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate regDate;
}
