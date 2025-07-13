package com.runcombi.server.domain.pet.repository;

import com.runcombi.server.domain.member.entity.Member;
import com.runcombi.server.domain.pet.entity.Pet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PetRepository extends JpaRepository<Pet, Long> {
    List<Pet> findByMember(Member member);
}
