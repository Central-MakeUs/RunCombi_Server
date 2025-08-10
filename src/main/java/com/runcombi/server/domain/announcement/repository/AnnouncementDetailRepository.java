package com.runcombi.server.domain.announcement.repository;

import com.runcombi.server.domain.announcement.entity.AnnouncementDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnnouncementDetailRepository extends JpaRepository<AnnouncementDetail, Long> {
}
