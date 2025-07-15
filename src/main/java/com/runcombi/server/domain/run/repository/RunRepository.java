package com.runcombi.server.domain.run.repository;

import com.runcombi.server.domain.run.entity.Run;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RunRepository extends JpaRepository<Run, Long> {

}
