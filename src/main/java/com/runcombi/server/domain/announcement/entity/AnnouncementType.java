package com.runcombi.server.domain.announcement.entity;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "공지 유형 (NOTICE: 공지, EVENT: 이벤트)")
public enum AnnouncementType {
    NOTICE, EVENT
}
