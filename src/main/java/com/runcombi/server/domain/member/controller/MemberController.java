package com.runcombi.server.domain.member.controller;

import com.runcombi.server.domain.member.dto.AgreeTermsRequestDto;
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
            @RequestPart("pet") SetPetDetailDto  petDetail,
            @RequestPart(value = "petImage", required = false) MultipartFile petImage
            ) {
        memberService.setMemberPetDetail(member, memberDetail, memberImage, petDetail, petImage);
        return ApiResponse.onSuccess("정보 등록에 성공하셨습니다.");
    }

    @PostMapping("/member/getMemberDetail")
    public ApiResponse<GetMemberDetailDto> getMemberDetail(
            @AuthenticationPrincipal Member member
    ) {
        List<Pet> petList = petService.getPetList(member);
        GetMemberDetailDto getMemberDetailDto = memberService.getMemberPetDetail(member, petList);
        return ApiResponse.onSuccess(getMemberDetailDto);
    }

    @PostMapping("/member/setMemberTerms")
    public ApiResponse<String> setMemberTerms(
            @AuthenticationPrincipal Member member,
            @RequestBody AgreeTermsRequestDto agreeTermsList
    ) {
        memberService.setMemberTerms(agreeTermsList.getAgreeTermList(), member);

        return ApiResponse.onSuccess("약관 동의 저장에 성공하였습니다.");
    }

    @PostMapping("/member/updateMemberDetail")
    public ApiResponse<String> updateMemberDetail(
            @AuthenticationPrincipal Member member,
            @RequestPart(value = "updateMemberDetail") SetMemberDetailDto updateMemberDto,
            @RequestPart(required = false) MultipartFile memberImage
    ) {
        memberService.updateMemberDetail(member, updateMemberDto, memberImage);

        return ApiResponse.onSuccess("정보 수정에 성공하였습니다.");
    }

    @PostMapping("/member/deleteAccount")
    public ApiResponse<String> deleteAccount(
            @AuthenticationPrincipal Member member
    ) {
        memberService.deleteAccount(member);

        return ApiResponse.onSuccess("회원 탈퇴에 성공하였습니다.");
    }
}
