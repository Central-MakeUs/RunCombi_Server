package com.runcombi.server.domain.announcement.dto;

import com.runcombi.server.domain.announcement.entity.AnnouncementType;
import com.runcombi.server.domain.announcement.entity.Display;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class RequestUpdateAnnouncementDto {
    private Long announcementId;                    // 공지 Id
    private Display display;                        // 노출 여부
    private LocalDate startDate;                    // 시작 일자
    private LocalDate endDate;                      // 종료 일자
    private String title;                           // 제목
    private String content;                         // 내용
    private String announcementImageUrl;            // 공지 및 이벤트 이미지 URL
    private String eventApplyUrl;                   // 이벤트 참여 URL
}
