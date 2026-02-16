package com.runcombi.server.domain.announcement.dto;

import com.runcombi.server.domain.announcement.entity.AnnouncementType;
import com.runcombi.server.domain.announcement.entity.Display;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Schema(description = "공지/이벤트 등록 요청 DTO")
public class RequestAddAnnouncementDto {
    @Schema(description = "공지 유형", example = "NOTICE")
    private AnnouncementType announcementType;      // 공지(NOTICE), 이벤트(EVENT)
    @Schema(description = "노출 여부", example = "Y")
    private Display display;                        // 노출 여부
    @Schema(description = "노출 시작일", example = "2026-02-16")
    private LocalDate startDate;                    // 시작 일자
    @Schema(description = "노출 종료일", example = "2026-02-28")
    private LocalDate endDate;                      // 종료 일자
    @Schema(description = "제목", example = "2월 정기 점검 안내")
    private String title;                           // 제목
    @Schema(description = "본문 내용", example = "서비스 안정화를 위한 점검이 진행됩니다.")
    private String content;                         // 내용
    @Schema(description = "공지/이벤트 이미지 URL", example = "https://cdn.runcombi.com/announcement/a1.png")
    private String announcementImageUrl;            // 공지 및 이벤트 이미지 URL
    @Schema(description = "이벤트 참여 URL(이벤트가 아닌 경우 null 가능)", example = "https://event.runcombi.com/apply")
    private String eventApplyUrl;                   // 이벤트 참여 URL
}
