package com.runcombi.server.domain.run.service;

import com.runcombi.server.domain.member.entity.Member;
import com.runcombi.server.domain.member.repository.MemberRepository;
import com.runcombi.server.domain.pet.entity.Pet;
import com.runcombi.server.domain.pet.entity.RunStyle;
import com.runcombi.server.domain.pet.repository.PetRepository;
import com.runcombi.server.domain.run.dto.PetCalDto;
import com.runcombi.server.domain.run.dto.RequestEndMemberRunDto;
import com.runcombi.server.domain.run.dto.RequestEndPetRunDto;
import com.runcombi.server.domain.run.dto.ResponseStartRunDto;
import com.runcombi.server.domain.run.entity.Run;
import com.runcombi.server.domain.run.entity.RunPet;
import com.runcombi.server.domain.run.repository.RunPetRepository;
import com.runcombi.server.domain.run.repository.RunRepository;
import com.runcombi.server.global.exception.CustomException;
import com.runcombi.server.global.s3.dto.S3ImageReturnDto;
import com.runcombi.server.global.s3.service.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
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
    private final S3Service s3Service;

    @Transactional
    public ResponseStartRunDto startRun(Member contextMember, List<Long> petList, RunStyle memberRunStyle) {
        if(petList == null || petList.isEmpty()) throw new CustomException(RUN_REQUIRE_PET);

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

        // Run 생성 후 저장
        Run run = runRepository.save(Run.builder()
                .member(member)
                .memberRunStyle(memberRunStyle)
                .build());

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

    @Transactional
    public void endRun(Member contextMember, RequestEndMemberRunDto memberRunData, RequestEndPetRunDto petRunDataList, MultipartFile routeImage, MultipartFile runImage) {
        Member member = memberRepository.findByMemberId(contextMember.getMemberId());

        // 해당 runId 가 없는 경우 RUN_ID_INVALID 예외 발생
        Run run = runRepository.findById(memberRunData.getRunId()).orElseThrow(() -> new CustomException(RUN_ID_INVALID));
        // runId 와 memberId 가 일치하지 않는 경우 RUN_ID_INVALID 에외 발생
        if(run.getMember() != member) throw new CustomException(RUN_ID_INVALID);

        // 회원의 Pet 인지 확인
        List<RunPet> runPets = run.getRunPets();
        Set<Long> petIdSet = runPets.stream()
                .map(RunPet::getPet)
                .map(Pet::getPetId)
                .collect(Collectors.toSet());
        System.out.println(petIdSet.size());
        List<PetCalDto> petCalList = petRunDataList.getPetCalList();
        List<Long> requestPetId = new ArrayList<>();
        System.out.println(requestPetId);
        for(PetCalDto petCalDto : petCalList) {
            requestPetId.add(petCalDto.getPetId());
        }
        for (Long id : requestPetId) {
            // 회원의 펫이 아닌 경우 예외 처리
            if (!petIdSet.contains(id)) {
                throw new CustomException(PET_NOT_MATCH);
            }
        }

        // 산책 경로 이미지 저장
        S3ImageReturnDto routeImageReturnDto = s3Service.uploadRouteImage(routeImage, run.getRunId());
        run.setRouteImage(routeImageReturnDto);

        // 이미지 등록
        if(runImage != null) {
            S3ImageReturnDto runImageReturnDto = s3Service.uploadRunImage(runImage, run.getRunId());
            run.setRunImage(runImageReturnDto);
        }

        // run 데이터 저장
        run.updateRun(
                memberRunData.getMemberCal(),
                memberRunData.getRunTime(),
                memberRunData.getRunDistance(),
                memberRunData.getRunEvaluating(),
                memberRunData.getMemo()
        );
        runRepository.save(run);

        // 각 펫 소모 칼로리 저장
        for(PetCalDto petCalDto : petCalList) {
            Pet pet = petRepository.findByPetId(petCalDto.getPetId());
            RunPet runPet = runPetRepository.findByRunAndPet(run, pet).orElseThrow(() -> new CustomException(RUN_ID_INVALID));
            runPet.updateCal(petCalDto.getPetCal());
            runPetRepository.save(runPet);
        }
    }

    public void setRunImage(Run run, S3ImageReturnDto memberImageReturnDto) {
        run.setRunImage(memberImageReturnDto);
    }
}
