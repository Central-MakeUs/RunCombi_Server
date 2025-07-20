package com.runcombi.server.domain.pet.service;

import com.runcombi.server.domain.member.entity.Member;
import com.runcombi.server.domain.member.repository.MemberRepository;
import com.runcombi.server.domain.pet.dto.SetPetDetailDto;
import com.runcombi.server.domain.pet.dto.UpdatePetDetailDto;
import com.runcombi.server.domain.pet.entity.Pet;
import com.runcombi.server.domain.pet.repository.PetRepository;
import com.runcombi.server.global.exception.CustomException;
import com.runcombi.server.global.s3.dto.S3ImageReturnDto;
import com.runcombi.server.global.s3.service.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.runcombi.server.global.exception.code.CustomErrorList.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class PetService {
    private final PetRepository petRepository;
    private final MemberRepository memberRepository;
    private final S3Service s3Service;
    @Transactional
    public List<Pet> getPetList(Member member) {
        return petRepository.findAllByMember(member);
    }

    public Pet setPetDetail(Member member, SetPetDetailDto petDetail) {
        Pet pet = new Pet();
        pet.setPetDetail(petDetail);
        member.addPet(pet);
        return pet;
    }

    public void setPetImage(Pet pet, S3ImageReturnDto petImageReturnDto) {
        pet.setPetImage(petImageReturnDto);
    }

    public void petDetailNullCheck(SetPetDetailDto petDetail) {
        if(
                petDetail == null ||
                        petDetail.getName() == null ||
                        petDetail.getWeight() == null ||
                        petDetail.getRunStyle() == null
        ) {
            throw new CustomException(DEFAULT_FIELD_NULL);
        }
    }

    @Transactional
    public void deletePetByMember(Pet pet, Member member) {
        // 펫 이미지와 펫 정보를 삭제
        if(pet.getPetImageKey() != null) {
            s3Service.deleteImage(pet.getPetImageKey());
        }
        petRepository.delete(pet);
        member.deletePet(pet);
    }

    @Transactional
    public void setMemberPetDetail(Member contextMember, SetPetDetailDto petDetail, MultipartFile petImage) {
        Member member = memberRepository.findByMemberId(contextMember.getMemberId());
        if(member.getPets().size() == 2) throw new CustomException(PET_COUNT_EXCEEDED);
        Pet pet = setPetDetail(member, petDetail);
        if(petImage != null) {
            S3ImageReturnDto petImageReturnDto = s3Service.uploadPetImage(petImage, pet.getPetId());
            setPetImage(pet, petImageReturnDto);
        };
    }

    @Transactional
    public void updatePetDetail(Member contextMember, UpdatePetDetailDto updatePetDetail, MultipartFile petImage) {
        Member member = memberRepository.findByMemberId(contextMember.getMemberId());
        Pet pet = petRepository.findByPetId(updatePetDetail.getPetId());

        // petId 에 해당하는 반려 동물이 존재하지 않는 경우 예외 처리
        if(pet == null) throw new CustomException(PET_ID_INVALID);
        // 회원의 반려 동물이 아닌경우 예외 처리
        if(member != pet.getMember()) throw new CustomException(PET_NOT_MATCH);

        pet.updatePetDetail(updatePetDetail);

        if(petImage != null) {
            if(pet.getPetImageKey() != null) {
                s3Service.deleteImage(pet.getPetImageKey());
            }
            S3ImageReturnDto petImageReturnDto = s3Service.uploadPetImage(petImage, pet.getPetId());
            pet.setPetImage(petImageReturnDto);
        }

        petRepository.save(pet);
    }

    @Transactional
    public void deletePet(Member contextMember, Long deletePetId) {
        Member member = memberRepository.findByMemberId(contextMember.getMemberId());
        Pet pet = petRepository.findByPetId(deletePetId);

        // 펫 수가 1마리 이하일 경우 예외 처리
        List<Pet> pets = member.getPets();
        if(pets.size() <= 1) throw new CustomException(PET_COUNT_MINIMUM);
        // petId 에 해당하는 반려 동물이 존재하지 않는 경우 예외 처리
        if(pet == null) throw new CustomException(PET_ID_INVALID);
        // 회원의 반려 동물이 아닌경우 예외 처리
        if(member != pet.getMember()) throw new CustomException(PET_NOT_MATCH);

        if(pet.getPetImageKey() != null) {
            s3Service.deleteImage(pet.getPetImageKey());
        }

        deletePetByMember(pet, member);
    }
}
