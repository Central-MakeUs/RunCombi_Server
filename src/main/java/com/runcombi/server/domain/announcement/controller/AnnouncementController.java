package com.runcombi.server.domain.announcement.controller;

import com.runcombi.server.domain.announcement.dto.RequestAddAnnouncementDto;
import com.runcombi.server.domain.announcement.dto.RequestAnnouncementIdDto;
import com.runcombi.server.domain.announcement.dto.ResponseAnnouncementDetailDto;
import com.runcombi.server.domain.announcement.dto.ResponseAnnouncementDto;
import com.runcombi.server.domain.announcement.service.AnnouncementService;
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
@RequestMapping("/announcement")
@RequiredArgsConstructor
public class AnnouncementController {
    private final AnnouncementService announcementService;
    @PostMapping("/addAnnouncement")
    public ApiResponse<String> addAnnouncement(
            @RequestBody RequestAddAnnouncementDto requestAddAnnouncementDto
            // TODO : 운영자인지 확인 이후 작업 허용 코드 추가
    ) {
        announcementService.addAnnouncement(requestAddAnnouncementDto);

        return ApiResponse.onSuccess("등록에 성공하였습니다.");
    }

    @PostMapping("/deleteAnnouncement")
    public ApiResponse<String> deleteAnnouncement(
            @RequestBody RequestAnnouncementIdDto requestAnnouncementIdDto
            // TODO : 운영자인지 확인 이후 작업 허용 코드 추가
    ) {
        announcementService.deleteAnnouncement(requestAnnouncementIdDto.getAnnouncementId());

        return ApiResponse.onSuccess("삭제에 성공하였습니다.");
    }

    @PostMapping("/getAnnouncementList")
    public ApiResponse<List<ResponseAnnouncementDto>> getAnnouncementList(
            @AuthenticationPrincipal Member member
    ) {
        List<ResponseAnnouncementDto> announcementList = announcementService.getAnnouncementList(member);

        return ApiResponse.onSuccess(announcementList);
    }

    @PostMapping("/getAnnouncementDetail")
    public ApiResponse<ResponseAnnouncementDetailDto> getAnnouncementDetail(
            @AuthenticationPrincipal Member member,
            @RequestBody RequestAnnouncementIdDto requestAnnouncementIdDto
    ) {
        ResponseAnnouncementDetailDto announcementDetail = announcementService.getAnnouncementDetail(member, requestAnnouncementIdDto.getAnnouncementId());
        return ApiResponse.onSuccess(announcementDetail);
    }
}
