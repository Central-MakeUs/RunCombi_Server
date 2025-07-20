package com.runcombi.server.domain.pet.controller;

import com.runcombi.server.domain.member.dto.SetMemberDetailDto;
import com.runcombi.server.domain.member.entity.Member;
import com.runcombi.server.domain.pet.dto.DeletePetDto;
import com.runcombi.server.domain.pet.dto.SetPetDetailDto;
import com.runcombi.server.domain.pet.dto.UpdatePetDetailDto;
import com.runcombi.server.domain.pet.service.PetService;
import com.runcombi.server.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PetController {
    private final PetService petService;
    @PostMapping("/pet/addPet")
    public ApiResponse<String> addPet(
            @AuthenticationPrincipal Member member,
            @RequestPart("pet") SetPetDetailDto petDetail,
            @RequestPart(value = "petImage", required = false) MultipartFile petImage
    ) {
        petService.setMemberPetDetail(member, petDetail, petImage);
        return ApiResponse.onSuccess("정보 등록에 성공하셨습니다.");
    }

    @PostMapping("/pet/updatePetDetail")
    public ApiResponse<String> updatePetDetail(
            @AuthenticationPrincipal Member member,
            @RequestPart("updatePetDetail") UpdatePetDetailDto updatePetDetail,
            @RequestPart(value = "petImage", required = false) MultipartFile petImage
    ) {
        petService.updatePetDetail(member, updatePetDetail, petImage);
        return ApiResponse.onSuccess("정보 수정에 성공하셨습니다.");
    }

    @PostMapping("/pet/deletePet")
    public ApiResponse<String> deletePet(
            @AuthenticationPrincipal Member member,
            @RequestBody DeletePetDto deletePetRequest
    ) {
        petService.deletePet(member, deletePetRequest.getDeletePetId());
        return ApiResponse.onSuccess("삭제에 성공하셨습니다.");
    }
}
