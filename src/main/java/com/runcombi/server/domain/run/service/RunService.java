package com.runcombi.server.domain.run.service;

import com.runcombi.server.domain.member.entity.Member;
import com.runcombi.server.domain.member.repository.MemberRepository;
import com.runcombi.server.domain.pet.entity.Pet;
import com.runcombi.server.domain.pet.repository.PetRepository;
import com.runcombi.server.domain.run.dto.ResponseStartRunDto;
import com.runcombi.server.domain.run.entity.Run;
import com.runcombi.server.domain.run.entity.RunPet;
import com.runcombi.server.domain.run.repository.RunPetRepository;
import com.runcombi.server.domain.run.repository.RunRepository;
import com.runcombi.server.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.runcombi.server.global.exception.code.CustomErrorList.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class RunService {
    private final RunPetRepository runPetRepository;
    private final RunRepository runRepository;
    private final PetRepository petRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public ResponseStartRunDto startRun(Member contextMember, List<Long> petList) {
        Member member = memberRepository.findByMemberId(contextMember.getMemberId());
        List<Pet> pets = member.getPets();

        // Pet 유효성 검사 - Start
        // 넘겨받은 petId 가 현재 가진 펫 정보와 일치하는지 검사
        Set<Long> petIdSet = pets.stream()
                .map(Pet::getPetId)
                .collect(Collectors.toSet());

        for (Long id : petList) {
            // 회원의 펫이 아닌 경우 예외 처리
            if (!petIdSet.contains(id)) {
                throw new CustomException(PET_NOT_MATCH);
            }
        }

        Run run = Run.builder()
                .member(member)
                .build();
        runRepository.save(run);

        for(Long id : petList) {
            for(Pet pet : pets) {
                if(Objects.equals(id, pet.getPetId())) {
                    RunPet runPet = RunPet.builder().build();
                    runPetRepository.save(runPet);
                    pet.setRunPets(runPet);
                    run.setRunPets(runPet);
                }
            }
        }

        return ResponseStartRunDto.builder().runId(run.getRunId()).build();
    }
}
