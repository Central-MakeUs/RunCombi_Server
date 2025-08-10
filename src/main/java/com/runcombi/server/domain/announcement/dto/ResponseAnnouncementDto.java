package com.runcombi.server.domain.announcement.dto;

import com.runcombi.server.domain.announcement.entity.AnnouncementType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@Getter
public class ResponseAnnouncementDto {
    private Long announcementId;
    private AnnouncementType announcementType;
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate regDate;
    private String isRead;
}
