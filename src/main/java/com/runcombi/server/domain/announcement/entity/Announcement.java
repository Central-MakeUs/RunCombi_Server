package com.runcombi.server.domain.announcement.entity;

import com.runcombi.server.domain.base.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Announcement extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long announcementId;

    @OneToMany(mappedBy = "announcement", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<AnnouncementView> announcementViewList = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "announcementDetailId")
    private AnnouncementDetail announcementDetail;

    @Enumerated(EnumType.STRING)
    private AnnouncementType announcementType; // NOTICE(공지), EVENT(이벤트)

    @Enumerated(EnumType.STRING)
    private Display display; // 노출 여부 (Y, N)

    private String title; // 공지사항 및 이벤트 타이틀(제목)

    private LocalDate startDate; // 이벤트, 공지 시작일자 ex) 2025-08-09
    private LocalDate endDate; // 이벤트, 공지 종료일자 ex) 2025-09-09

    public void addAnnouncementView(AnnouncementView announcementView) {
        this.announcementViewList.add(announcementView);
        announcementView.setAnnouncement(this);
    }
}
