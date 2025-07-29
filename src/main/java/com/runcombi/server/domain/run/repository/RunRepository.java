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

    // 특정 달의 runId 와 regDate 를 리스트로 반환
    @Query("SELECT r.runId, r.regDate FROM Run r WHERE r.member = :member AND r.regDate >= :start AND r.regDate < :end")
    List<Object[]> findRunIdsAndDates(@Param("member") Member member, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    void deleteByMember(Member member);

    // 한 달 평균 산책 시간 반환 (소수점 버림)
    @Query("SELECT FLOOR(AVG(r.runTime)) FROM Run r WHERE r.member = :member AND r.regDate >= :start AND r.regDate < :end")
    Integer findAverageMemberRunTime(@Param("member") Member member, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    // 한 달 소모 칼로리 평균을 반환 (소수점 버림)
    @Query("SELECT FLOOR(AVG(r.memberCal)) FROM Run r WHERE r.member = :member AND r.regDate >= :start AND r.regDate < :end")
    Integer findAverageMemberCal(@Param("member") Member member, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    // 한 달 평균 산책 거리 반환 (소수점 2번째 자리까지)
    @Query("SELECT ROUND(AVG(r.runDistance), 2) FROM Run r WHERE r.member = :member AND r.regDate >= :start AND r.regDate < :end")
    Double findAverageRunDistance(@Param("member") Member member, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    // 한 달 가장 많은 산책 스타일
    @Query(value = "SELECT member_run_style, COUNT(*) AS cnt FROM run " +
            "WHERE member_id = :memberId AND reg_date >= :start AND reg_date < :end AND member_run_style IS NOT NULL " +
            "GROUP BY member_run_style " +
            "ORDER BY cnt DESC, " +
            "CASE member_run_style WHEN 'RUNNING' THEN 1 WHEN 'WALKING' THEN 2 WHEN 'SLOW_WALKING' THEN 3 END",
            nativeQuery = true)
    List<Object[]> findRunStyleCounts(@Param("memberId") Long memberId,
                                      @Param("start") LocalDateTime start,
                                      @Param("end") LocalDateTime end);

    // 일자로 산책 데이터를 가져오기
    List<Run> findByMemberAndRegDateBetween(Member member, LocalDateTime start, LocalDateTime end);
}
