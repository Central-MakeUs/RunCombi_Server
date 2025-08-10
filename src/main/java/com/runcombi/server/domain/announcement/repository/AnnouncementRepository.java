package com.runcombi.server.domain.announcement.repository;

import com.runcombi.server.domain.announcement.entity.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
    @Query("SELECT a FROM Announcement a WHERE a.display = 'Y' AND :today BETWEEN a.startDate AND a.endDate")
    List<Announcement> findActiveAnnouncementList(@Param("today") LocalDate today);
}
