package com.runcombi.server.domain.run.controller;

import com.runcombi.server.domain.member.entity.Member;
import com.runcombi.server.domain.run.dto.*;
import com.runcombi.server.domain.run.service.RunService;
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
public class RunController {
    private final RunService runService;

    @PostMapping("/run/startRun")
    public ApiResponse<ResponseStartRunDto> startRun(
            @AuthenticationPrincipal Member member,
            @RequestBody RequestStartRunDto requestStartRunDto
    ) {
        ResponseStartRunDto responseStartRunDto = runService.startRun(member, requestStartRunDto.getPetList(), requestStartRunDto.getMemberRunStyle());

        return ApiResponse.onSuccess(responseStartRunDto);
    }

    @PostMapping("/run/endRun")
    public ApiResponse<String> endRun(
            @AuthenticationPrincipal Member member,
            @RequestPart(value = "memberRunData") RequestEndMemberRunDto memberRunData,
            @RequestPart(value = "petRunData") RequestEndPetRunDto petRunDataList,
            @RequestPart MultipartFile routeImage,
            @RequestPart(required = false) MultipartFile runImage
    ) {
        runService.endRun(member, memberRunData, petRunDataList, routeImage, runImage);

        return ApiResponse.onSuccess("산책 정보 업데이트에 성공했습니다.");
    }
}
