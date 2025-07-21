package com.runcombi.server.domain.calender.controller;

import com.runcombi.server.domain.calender.dto.DateDto;
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
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CalenderController {
    private final CalenderService calenderService;
    @PostMapping("/calender/getMonthData")
    public ApiResponse<Object> getMonthData(
            @AuthenticationPrincipal Member member,
            @RequestBody DateDto dateDto
    ) {
        List<Map<String, List<Long>>> monthData = calenderService.getMonthData(member, dateDto.getYear(), dateDto.getMonth());
        return ApiResponse.onSuccess(monthData);
    }
}
