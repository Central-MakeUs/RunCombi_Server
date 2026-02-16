package com.runcombi.server.domain.announcement.controller;

import com.runcombi.server.domain.announcement.dto.RequestAddAnnouncementDto;
import com.runcombi.server.domain.announcement.dto.RequestAnnouncementIdDto;
import com.runcombi.server.domain.announcement.dto.ResponseAnnouncementDetailDto;
import com.runcombi.server.domain.announcement.dto.ResponseAnnouncementDto;
import com.runcombi.server.domain.announcement.service.AnnouncementService;
import com.runcombi.server.domain.member.entity.Member;
import com.runcombi.server.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@Tag(name = "공지/이벤트", description = "공지사항 및 이벤트 조회 API")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/announcement")
@RequiredArgsConstructor
public class AnnouncementController {
    private final AnnouncementService announcementService;
    /*@PostMapping("/addAnnouncement")
    public ApiResponse<String> addAnnouncement(
            @RequestBody RequestAddAnnouncementDto requestAddAnnouncementDto
    ) {
        announcementService.addAnnouncement(requestAddAnnouncementDto);

        return ApiResponse.onSuccess("등록에 성공하였습니다.");
    }*/

    /*@PostMapping("/deleteAnnouncement")
    public ApiResponse<String> deleteAnnouncement(
            @RequestBody RequestAnnouncementIdDto requestAnnouncementIdDto
            // TODO : 운영자인지 확인 이후 작업 허용 코드 추가
    ) {
        announcementService.deleteAnnouncement(requestAnnouncementIdDto.getAnnouncementId());

        return ApiResponse.onSuccess("삭제에 성공하였습니다.");
    }*/

    @Operation(
            summary = "공지/이벤트 목록 조회",
            description = "현재 노출 가능한 공지/이벤트 목록을 조회합니다.\n" +
                    "각 항목의 `isRead` 값으로 로그인 회원의 열람 여부(`Y`/`N`)를 함께 반환합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "목록 조회 성공",
                    content = @Content(
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "성공 예시",
                                    value = """
                                            {
                                              "isSuccess": true,
                                              "code": "STATUS200",
                                              "message": "요청에 성공하셨습니다.",
                                              "result": [
                                                {
                                                  "announcementId": 101,
                                                  "announcementType": "NOTICE",
                                                  "title": "서비스 점검 안내",
                                                  "startDate": "2026-02-16",
                                                  "endDate": "2026-02-20",
                                                  "regDate": "2026-02-15",
                                                  "isRead": "N"
                                                }
                                              ]
                                            }
                                            """
                            )
                    )),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "토큰 오류 (TOKEN0001~TOKEN0003)",
                    content = @Content(
                            examples = @ExampleObject(
                                    name = "실패 예시",
                                    value = """
                                            {
                                              "isSuccess": false,
                                              "code": "TOKEN0001",
                                              "message": "토큰값이 존재하지 않습니다.",
                                              "result": null
                                            }
                                            """
                            )
                    ))
    })
    @PostMapping("/getAnnouncementList")
    public ApiResponse<List<ResponseAnnouncementDto>> getAnnouncementList(
            @Parameter(hidden = true) @AuthenticationPrincipal Member member
    ) {
        List<ResponseAnnouncementDto> announcementList = announcementService.getAnnouncementList(member);

        return ApiResponse.onSuccess(announcementList);
    }

    @Operation(
            summary = "공지/이벤트 상세 조회",
            description = "공지/이벤트 상세 정보를 조회합니다.\n" +
                    "이벤트 타입인 경우 회원별 참여 코드를 생성/저장하여 `code` 필드로 반환합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "상세 조회 성공",
                    content = @Content(
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "성공 예시",
                                    value = """
                                            {
                                              "isSuccess": true,
                                              "code": "STATUS200",
                                              "message": "요청에 성공하셨습니다.",
                                              "result": {
                                                "announcementId": 101,
                                                "announcementType": "EVENT",
                                                "title": "봄맞이 산책 챌린지",
                                                "content": "참여만 해도 포인트 지급",
                                                "announcementImageUrl": "https://cdn.runcombi.com/announcement/event.png",
                                                "code": "4f8c1f1d...",
                                                "eventApplyUrl": "https://event.runcombi.com/apply",
                                                "startDate": "2026-03-01",
                                                "endDate": "2026-03-31",
                                                "regDate": "2026-02-20"
                                              }
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
                    )),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "토큰 오류 (TOKEN0001~TOKEN0003)",
                    content = @Content(
                            examples = @ExampleObject(
                                    name = "토큰 실패 예시",
                                    value = """
                                            {
                                              "isSuccess": false,
                                              "code": "TOKEN0003",
                                              "message": "만료된 AccessToken 입니다.",
                                              "result": null
                                            }
                                            """
                            )
                    ))
    })
    @PostMapping("/getAnnouncementDetail")
    public ApiResponse<ResponseAnnouncementDetailDto> getAnnouncementDetail(
            @Parameter(hidden = true) @AuthenticationPrincipal Member member,
            @RequestBody RequestAnnouncementIdDto requestAnnouncementIdDto
    ) {
        ResponseAnnouncementDetailDto announcementDetail = announcementService.getAnnouncementDetail(member, requestAnnouncementIdDto.getAnnouncementId());
        return ApiResponse.onSuccess(announcementDetail);
    }
}
