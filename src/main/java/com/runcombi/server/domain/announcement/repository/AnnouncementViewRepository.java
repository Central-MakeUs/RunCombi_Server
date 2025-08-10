package com.runcombi.server.domain.announcement.repository;

import com.runcombi.server.domain.announcement.entity.Announcement;
import com.runcombi.server.domain.announcement.entity.AnnouncementView;
import com.runcombi.server.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AnnouncementViewRepository extends JpaRepository<AnnouncementView, Long> {
    Optional<AnnouncementView> findByAnnouncementAndMember(Announcement announcement, Member member);
}
