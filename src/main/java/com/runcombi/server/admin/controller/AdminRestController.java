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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "관리자", description = "관리자 전용 공지/회원/버전 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminRestController {

    private final AnnouncementService announcementService;
    private final MemberService memberService;
    private final VersionService versionService;

    @Operation(
            summary = "관리자 공지/이벤트 등록",
            description = "관리자가 공지 또는 이벤트를 신규 등록합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "등록 성공",
                    content = @Content(
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "성공 예시",
                                    value = """
                                            {
                                              "isSuccess": true,
                                              "code": "STATUS200",
                                              "message": "요청에 성공하셨습니다.",
                                              "result": "등록에 성공하였습니다."
                                            }
                                            """
                            )
                    )),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "관리자 인증 실패",
                    content = @Content(
                            examples = @ExampleObject(
                                    name = "실패 예시",
                                    value = """
                                            {
                                              "isSuccess": false,
                                              "code": "ADMIN0001",
                                              "message": "올바르지 않은 관리자 정보입니다.",
                                              "result": null
                                            }
                                            """
                            )
                    ))
    })
    @PostMapping("/addAnnouncement")
    public ApiResponse<String> addAnnouncement(
            @RequestBody RequestAddAnnouncementDto requestAddAnnouncementDto
    ) {
        announcementService.addAnnouncement(requestAddAnnouncementDto);

        return ApiResponse.onSuccess("등록에 성공하였습니다.");
    }

    @Operation(
            summary = "관리자 공지/이벤트 삭제",
            description = "관리자가 공지 또는 이벤트를 삭제합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "삭제 성공",
                    content = @Content(
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "성공 예시",
                                    value = """
                                            {
                                              "isSuccess": true,
                                              "code": "STATUS200",
                                              "message": "요청에 성공하셨습니다.",
                                              "result": "삭제에 성공하였습니다."
                                            }
                                            """
                            )
                    )),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "유효하지 않은 공지 ID (ANNOUNCEMENT0001)",
                    content = @Content(
                            examples = @ExampleObject(
                                    name = "실패 예시",
                                    value = """
                                            {
                                              "isSuccess": false,
                                              "code": "ANNOUNCEMENT0001",
                                              "message": "해당 공지사항 및 이벤트를 찾을 수 없습니다.",
                                              "result": null
                                            }
                                            """
                            )
                    ))
    })
    @PostMapping("/deleteAnnouncement")
    public ApiResponse<String> deleteAnnouncement(
            @RequestBody RequestAnnouncementIdDto requestAnnouncementIdDto
    ) {
        announcementService.deleteAnnouncement(requestAnnouncementIdDto.getAnnouncementId());

        return ApiResponse.onSuccess("삭제에 성공하였습니다.");
    }

    @Operation(
            summary = "관리자 공지/이벤트 수정",
            description = "관리자가 기존 공지/이벤트의 노출 상태, 기간, 제목/내용, 링크 정보를 수정합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "수정 성공",
                    content = @Content(
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "성공 예시",
                                    value = """
                                            {
                                              "isSuccess": true,
                                              "code": "STATUS200",
                                              "message": "요청에 성공하셨습니다.",
                                              "result": "삭제에 성공하였습니다."
                                            }
                                            """
                            )
                    )),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "유효하지 않은 공지 ID (ANNOUNCEMENT0001)",
                    content = @Content(
                            examples = @ExampleObject(
                                    name = "실패 예시",
                                    value = """
                                            {
                                              "isSuccess": false,
                                              "code": "ANNOUNCEMENT0001",
                                              "message": "해당 공지사항 및 이벤트를 찾을 수 없습니다.",
                                              "result": null
                                            }
                                            """
                            )
                    ))
    })
    @PostMapping("/updateAnnouncement")
    public ApiResponse<String> updateAnnouncement(
            @RequestBody RequestUpdateAnnouncementDto requestUpdateAnnouncementDto
    ) {
        announcementService.updateAnnouncement(requestUpdateAnnouncementDto);

        return ApiResponse.onSuccess("삭제에 성공하였습니다.");
    }

    @Operation(
            summary = "관리자 회원 삭제",
            description = "관리자가 특정 회원을 강제 삭제합니다. 회원 연동 해제 및 관련 이미지/산책 데이터도 함께 정리됩니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "삭제 성공",
                    content = @Content(
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "성공 예시",
                                    value = """
                                            {
                                              "isSuccess": true,
                                              "code": "STATUS200",
                                              "message": "요청에 성공하셨습니다.",
                                              "result": "삭제에 성공하였습니다."
                                            }
                                            """
                            )
                    )),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "연동 해제 실패 (KAKAO0004, APPLE0001)",
                    content = @Content(
                            examples = @ExampleObject(
                                    name = "실패 예시",
                                    value = """
                                            {
                                              "isSuccess": false,
                                              "code": "KAKAO0004",
                                              "message": "KAKAO 회원 탈퇴 요청에 실패했습니다.",
                                              "result": null
                                            }
                                            """
                            )
                    ))
    })
    @PostMapping("/deleteMember")
    public ApiResponse<String> deleteMember(
            @RequestBody RequestMemberIdDto requestMemberIdDto
    ) {
        memberService.deleteAccount(requestMemberIdDto.getMemberId());

        return ApiResponse.onSuccess("삭제에 성공하였습니다.");
    }

    @Operation(
            summary = "관리자 최소 버전 등록",
            description = "관리자가 OS별 최소 지원 버전과 업데이트 내역을 등록합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "버전 등록 성공",
                    content = @Content(
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "성공 예시",
                                    value = """
                                            {
                                              "isSuccess": true,
                                              "code": "STATUS200",
                                              "message": "요청에 성공하셨습니다.",
                                              "result": "버전 변경에 성공하였습니다."
                                            }
                                            """
                            )
                    )),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "관리자 인증 실패",
                    content = @Content(
                            examples = @ExampleObject(
                                    name = "실패 예시",
                                    value = """
                                            {
                                              "isSuccess": false,
                                              "code": "ADMIN0001",
                                              "message": "올바르지 않은 관리자 정보입니다.",
                                              "result": null
                                            }
                                            """
                            )
                    ))
    })
    @PostMapping("/updateVersion")
    public ApiResponse<String> deleteMember(
            @RequestBody RequestVersionDto requestVersionDto
    ) {
        System.out.println(requestVersionDto.getUpdateDetail());
        versionService.updateVersion(requestVersionDto);


        return ApiResponse.onSuccess("버전 변경에 성공하였습니다.");
    }
}
