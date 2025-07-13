package com.runcombi.server.domain.pet.service;

import com.runcombi.server.domain.member.entity.Member;
import com.runcombi.server.domain.pet.entity.Pet;
import com.runcombi.server.domain.pet.repository.PetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PetService {
    private final PetRepository petRepository;
    @Transactional
    public List<Pet> getPetList(Member member) {
        return petRepository.findByMember(member);
    }
}
