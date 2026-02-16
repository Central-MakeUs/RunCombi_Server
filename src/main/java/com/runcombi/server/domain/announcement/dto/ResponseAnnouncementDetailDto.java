package com.runcombi.server.domain.announcement.dto;

import com.runcombi.server.domain.announcement.entity.AnnouncementType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Builder
@Getter
@Schema(description = "공지/이벤트 상세 응답 DTO")
public class ResponseAnnouncementDetailDto {
    @Schema(description = "공지/이벤트 ID", example = "101")
    private Long announcementId;
    @Schema(description = "공지 유형", example = "EVENT")
    private AnnouncementType announcementType;
    @Schema(description = "제목", example = "봄맞이 산책 챌린지")
    private String title;
    @Schema(description = "본문 내용", example = "참여만 해도 포인트 지급!")
    private String content;
    @Schema(description = "공지/이벤트 이미지 URL", example = "https://cdn.runcombi.com/announcement/event.png")
    private String announcementImageUrl;
    @Schema(description = "이벤트 참여 코드(공지 유형은 null 가능)", example = "b19f4a...")
    private String code;
    @Schema(description = "이벤트 참여 URL", example = "https://event.runcombi.com/apply")
    private String eventApplyUrl;
    @Schema(description = "노출 시작일", example = "2026-03-01")
    private LocalDate startDate;
    @Schema(description = "노출 종료일", example = "2026-03-31")
    private LocalDate endDate;
    @Schema(description = "등록일", example = "2026-02-20")
    private LocalDate regDate;
}
