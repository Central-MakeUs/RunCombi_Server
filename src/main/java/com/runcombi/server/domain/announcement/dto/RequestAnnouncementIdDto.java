package com.runcombi.server.domain.announcement.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "공지/이벤트 ID 요청 DTO")
public class RequestAnnouncementIdDto {
    @Schema(description = "공지/이벤트 ID", example = "101")
    private Long announcementId;
}
