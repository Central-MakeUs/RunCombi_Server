package com.runcombi.server.domain.calender.controller;

import com.runcombi.server.domain.calender.dto.*;
import com.runcombi.server.domain.calender.service.CalenderService;
import com.runcombi.server.domain.member.entity.Member;
import com.runcombi.server.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CalenderController {
    private final CalenderService calenderService;
    @PostMapping("/calender/getMonthData")
    public ApiResponse<Object> getMonthData(
            @AuthenticationPrincipal Member member,
            @RequestBody RequestDateDto requestDateDto
    ) {
        ResponseMonthRunDto monthData = calenderService.getMonthData(member, requestDateDto.getYear(), requestDateDto.getMonth());
        return ApiResponse.onSuccess(monthData);
    }

    @PostMapping("/calender/getDayData")
    public ApiResponse<List<ResponseDayRunDto>> getDayData(
            @AuthenticationPrincipal Member member,
            @RequestBody RequestDateDto requestDateDto
    ) {
        List<ResponseDayRunDto> dayDataList = calenderService.getDayData(member, requestDateDto.getYear(), requestDateDto.getMonth(), requestDateDto.getDay());
        return ApiResponse.onSuccess(dayDataList);
    }

    @PostMapping("/calender/getRunData")
    public ApiResponse<ResponseRunDto> getRunData(
            @AuthenticationPrincipal Member member,
            @RequestBody RequestRunIdDto requestRunIdDto
    ) {
        ResponseRunDto runData = calenderService.getRunData(member, requestRunIdDto.getRunId());
        return ApiResponse.onSuccess(runData);
    }

    @PostMapping("/calender/setRunEvaluating")
    public ApiResponse<String> setRunEvaluating(
            @AuthenticationPrincipal Member member,
            @RequestBody RequestRunEvaluatingDto runEvaluatingDto
    ) {
        calenderService.setRunEvaluating(member, runEvaluatingDto.getRunId(), runEvaluatingDto.getRunEvaluating());
        return ApiResponse.onSuccess("산책 평가 등록에 성공하였습니다.");
    }
}
