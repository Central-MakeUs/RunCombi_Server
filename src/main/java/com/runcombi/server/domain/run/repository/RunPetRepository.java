package com.runcombi.server.domain.run.repository;

import com.runcombi.server.domain.pet.entity.Pet;
import com.runcombi.server.domain.run.entity.Run;
import com.runcombi.server.domain.run.entity.RunPet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RunPetRepository extends JpaRepository<RunPet, Long> {
    Optional<RunPet> findByRunAndPet(Run run, Pet pet);

}
