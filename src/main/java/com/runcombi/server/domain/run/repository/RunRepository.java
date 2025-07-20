package com.runcombi.server.domain.run.repository;

import com.runcombi.server.domain.member.entity.Member;
import com.runcombi.server.domain.run.entity.Run;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface RunRepository extends JpaRepository<Run, Long> {
    List<Run> findByMember(Member member);

    // member와 이번 달 기록 수 조회
    @Query("SELECT COUNT(r) FROM Run r WHERE r.member = :member AND r.regDate >= :startDate AND r.regDate < :endDate")
    int countByMemberAndMonth(@Param("member") Member member, @Param("startDate") LocalDateTime start, @Param("endDate") LocalDateTime end);
}
