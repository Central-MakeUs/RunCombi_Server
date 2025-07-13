package com.runcombi.server.domain.pet.service;

import com.runcombi.server.domain.member.entity.Member;
import com.runcombi.server.domain.pet.dto.SetPetDetailDto;
import com.runcombi.server.domain.pet.entity.Pet;
import com.runcombi.server.domain.pet.repository.PetRepository;
import com.runcombi.server.global.exception.CustomException;
import com.runcombi.server.global.s3.dto.S3ImageReturnDto;
import com.runcombi.server.global.s3.service.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.runcombi.server.global.exception.code.CustomErrorList.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class PetService {
    private final PetRepository petRepository;
    private final S3Service s3Service;
    @Transactional
    public List<Pet> getPetList(Member member) {
        return petRepository.findByMember(member);
    }

    @Transactional
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
    public void deletePet(Pet pet) {
        s3Service.deleteImage(pet.getPetImageKey());
        petRepository.delete(pet);
    }
}
