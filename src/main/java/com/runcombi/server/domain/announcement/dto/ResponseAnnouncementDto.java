package com.runcombi.server.domain.announcement.dto;

import com.runcombi.server.domain.announcement.entity.AnnouncementType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@Getter
@Schema(description = "공지/이벤트 목록 항목 응답 DTO")
public class ResponseAnnouncementDto {
    @Schema(description = "공지/이벤트 ID", example = "101")
    private Long announcementId;
    @Schema(description = "공지 유형", example = "EVENT")
    private AnnouncementType announcementType;
    @Schema(description = "제목", example = "봄맞이 산책 챌린지")
    private String title;
    @Schema(description = "노출 시작일", example = "2026-03-01")
    private LocalDate startDate;
    @Schema(description = "노출 종료일", example = "2026-03-31")
    private LocalDate endDate;
    @Schema(description = "등록일", example = "2026-02-20")
    private LocalDate regDate;
    @Schema(description = "열람 여부(Y/N)", example = "N")
    private String isRead;
}
