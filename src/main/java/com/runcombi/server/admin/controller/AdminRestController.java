package com.runcombi.server.admin.controller;

import com.runcombi.server.domain.announcement.dto.RequestAddAnnouncementDto;
import com.runcombi.server.domain.announcement.dto.RequestAnnouncementIdDto;
import com.runcombi.server.domain.announcement.dto.RequestUpdateAnnouncementDto;
import com.runcombi.server.domain.announcement.service.AnnouncementService;
import com.runcombi.server.domain.member.dto.RequestMemberIdDto;
import com.runcombi.server.domain.member.service.MemberService;
import com.runcombi.server.domain.version.dto.RequestVersionDto;
import com.runcombi.server.domain.version.service.VersionService;
import com.runcombi.server.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminRestController {

    private final AnnouncementService announcementService;
    private final MemberService memberService;
    private final VersionService versionService;

    @PostMapping("/addAnnouncement")
    public ApiResponse<String> addAnnouncement(
            @RequestBody RequestAddAnnouncementDto requestAddAnnouncementDto
    ) {
        announcementService.addAnnouncement(requestAddAnnouncementDto);

        return ApiResponse.onSuccess("등록에 성공하였습니다.");
    }

    @PostMapping("/deleteAnnouncement")
    public ApiResponse<String> deleteAnnouncement(
            @RequestBody RequestAnnouncementIdDto requestAnnouncementIdDto
    ) {
        announcementService.deleteAnnouncement(requestAnnouncementIdDto.getAnnouncementId());

        return ApiResponse.onSuccess("삭제에 성공하였습니다.");
    }

    @PostMapping("/updateAnnouncement")
    public ApiResponse<String> updateAnnouncement(
            @RequestBody RequestUpdateAnnouncementDto requestUpdateAnnouncementDto
    ) {
        announcementService.updateAnnouncement(requestUpdateAnnouncementDto);

        return ApiResponse.onSuccess("삭제에 성공하였습니다.");
    }

    @PostMapping("/deleteMember")
    public ApiResponse<String> deleteMember(
            @RequestBody RequestMemberIdDto requestMemberIdDto
    ) {
        memberService.deleteAccount(requestMemberIdDto.getMemberId());

        return ApiResponse.onSuccess("삭제에 성공하였습니다.");
    }

    @PostMapping("/updateVersion")
    public ApiResponse<String> deleteMember(
            @RequestBody RequestVersionDto requestVersionDto
    ) {
        System.out.println(requestVersionDto.getUpdateDetail());
        versionService.updateVersion(requestVersionDto);


        return ApiResponse.onSuccess("버전 변경에 성공하였습니다.");
    }
}
