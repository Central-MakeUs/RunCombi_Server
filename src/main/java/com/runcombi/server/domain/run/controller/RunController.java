package com.runcombi.server.domain.run.controller;

import com.runcombi.server.domain.member.entity.Member;
import com.runcombi.server.domain.run.dto.RequestStartRunDto;
import com.runcombi.server.domain.run.dto.ResponseStartRunDto;
import com.runcombi.server.domain.run.service.RunService;
import com.runcombi.server.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        ResponseStartRunDto responseStartRunDto = runService.startRun(member, requestStartRunDto.getPetList());

        return ApiResponse.onSuccess(responseStartRunDto);
    }
}
