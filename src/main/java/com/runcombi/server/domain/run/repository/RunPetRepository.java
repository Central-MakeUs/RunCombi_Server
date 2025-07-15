package com.runcombi.server.domain.run.repository;

import com.runcombi.server.domain.run.entity.RunPet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RunPetRepository extends JpaRepository<RunPet, Long> {
}
