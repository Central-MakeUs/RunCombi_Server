package com.runcombi.server.domain.member.controller;

import com.runcombi.server.domain.member.dto.SetMemberDetailDto;
import com.runcombi.server.domain.member.repository.MemberRepository;
import com.runcombi.server.domain.pet.dto.SetPetDetailDto;
import com.runcombi.server.global.exception.CustomException;
import com.runcombi.server.global.response.ApiResponse;
import com.runcombi.server.global.s3.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static com.runcombi.server.global.exception.code.CustomErrorList.MEMBER_NOT_FOUND;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestController {
    private final MemberRepository memberRepository;
    private final S3Service s3Service;

    @GetMapping("/fail")
    public void fail() {
        throw new CustomException(MEMBER_NOT_FOUND);
    }

    @GetMapping("/success")
    public ApiResponse<String> success() {
        return ApiResponse.onSuccess("성공입니다.");
    }

    @GetMapping("/success-list")
    public ApiResponse<List<String>> successList() {
        ArrayList<String> list = new ArrayList<>();
        list.add("첫번째");
        list.add("두번째");
        return ApiResponse.onSuccess(list);
    }

    @PostMapping("/member/setMemberDetail")
    public ApiResponse<String> setMemberDetail(
            @RequestPart(value = "memberImage", required = false) MultipartFile memberImage
    ) {
        s3Service.uploadMemberImage(memberImage, 5L);

        return ApiResponse.onSuccess("사용자 정보 등록에 성공하셨습니다.");
    }
}
