package com.runcombi.server.domain.announcement.entity;

import com.runcombi.server.domain.base.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnnouncementDetail extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long announcementDetailId;

    private String content; // 세부 글
    private String announcementImageUrl; // 공지, 이벤트 이미지 URL
    private String eventApplyUrl; // 이벤트 응모 페이지 URL
}
