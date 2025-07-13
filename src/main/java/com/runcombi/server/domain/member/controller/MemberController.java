package com.runcombi.server.domain.member.controller;

import com.runcombi.server.domain.member.dto.GetMemberDetailDto;
import com.runcombi.server.domain.member.dto.SetMemberDetailDto;
import com.runcombi.server.domain.member.entity.Member;
import com.runcombi.server.domain.member.entity.TermType;
import com.runcombi.server.domain.member.service.MemberService;
import com.runcombi.server.domain.pet.dto.SetPetDetailDto;
import com.runcombi.server.domain.pet.entity.Pet;
import com.runcombi.server.domain.pet.service.PetService;
import com.runcombi.server.global.response.ApiResponse;
import com.runcombi.server.global.s3.dto.S3ImageReturnDto;
import com.runcombi.server.global.s3.service.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;
    private final PetService petService;
    @PostMapping("/member/setMemberDetail")
    public ApiResponse<String> setMemberDetail(
            @AuthenticationPrincipal Member member,
            @RequestPart("memberDetail") SetMemberDetailDto memberDetail,
            @RequestPart(value = "memberImage", required = false) MultipartFile memberImage,
            @RequestPart("firstPet") SetPetDetailDto  firstPetDetail,
            @RequestPart(value = "firstPetImage", required = false) MultipartFile firstPetImage,
            @RequestPart(value = "secondPet", required = false) SetPetDetailDto  secondPetDetail,
            @RequestPart(value = "secondPetImage", required = false) MultipartFile secondPetImage
            ) {
        memberService.setMemberPetDetail(member, memberDetail, memberImage, firstPetDetail, firstPetImage, secondPetDetail, secondPetImage);
        return ApiResponse.onSuccess("정보 등록에 성공하셨습니다.");
    }

    @PostMapping("/member/getMemberDetail")
    public ApiResponse<GetMemberDetailDto> getMemberDetail(
            @AuthenticationPrincipal Member member
    ) {
        List<Pet> petList = petService.getPetList(member);
        GetMemberDetailDto getMemberDetailDto = GetMemberDetailDto.builder()
                .member(member)
                .petList(petList)
                .memberStatus(member.getIsActive())
                .build();
        return ApiResponse.onSuccess(getMemberDetailDto);
    }

    @PostMapping("/member/setMemberTerms")
    public ApiResponse<String> setMemberTerms(
            @AuthenticationPrincipal Member member,
            @RequestBody List<TermType> agreeTermsList
    ) {
        memberService.setMemberTerms(agreeTermsList, member);


        return ApiResponse.onSuccess("약관 동의 저장에 성공하였습니다.");
    }
}
