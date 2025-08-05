package com.runcombi.server.domain.version.repository;

import com.runcombi.server.domain.version.entity.OS;
import com.runcombi.server.domain.version.entity.Version;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VersionRepository extends JpaRepository<Version, Long> {
    /**
     * OS 별 가장 최신의 version 정보를 찾아오는 메소드
     * Optional 예외처리 필요
     * @param os
     * @return
     */
    Optional<Version> findTopByOsOrderByVersionIdDesc(OS os);
}
