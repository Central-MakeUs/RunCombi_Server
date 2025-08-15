package com.runcombi.server.admin.controller;

import com.runcombi.server.domain.announcement.dto.RequestAddAnnouncementDto;
import com.runcombi.server.domain.announcement.dto.RequestAnnouncementIdDto;
import com.runcombi.server.domain.announcement.service.AnnouncementService;
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
}
