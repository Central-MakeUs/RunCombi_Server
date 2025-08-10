package com.runcombi.server.domain.announcement.entity;

import com.runcombi.server.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnnouncementView {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long announcementViewId;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "announcement_id")
    private Announcement announcement;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Setter
    private String code; // 이벤트 코드
}
