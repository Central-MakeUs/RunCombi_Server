package com.runcombi.server.domain.version.controller;

import com.runcombi.server.domain.version.dto.RequestVersionDto;
import com.runcombi.server.domain.version.service.VersionService;
import com.runcombi.server.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/version")
@RequiredArgsConstructor
public class VersionController {
    private final VersionService versionService;
    @PostMapping("/check")
    public ApiResponse<Map<String, String>> versionCheck(
            @RequestBody RequestVersionDto requestVersionDto
    ) {
        Map<String, String> result = versionService.versionCheck(requestVersionDto.getOs(), requestVersionDto.getVersion());

        return ApiResponse.onSuccess(result);
    }
}
