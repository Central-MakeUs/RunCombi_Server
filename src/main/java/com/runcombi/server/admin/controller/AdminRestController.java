package com.runcombi.server.admin.controller;

import com.runcombi.server.admin.dto.UsageStatsBucketType;
import com.runcombi.server.admin.dto.UsageStatsResponseDto;
import com.runcombi.server.admin.service.AdminStatisticsService;
import com.runcombi.server.domain.announcement.dto.RequestAddAnnouncementDto;
import com.runcombi.server.domain.announcement.dto.RequestAnnouncementIdDto;
import com.runcombi.server.domain.announcement.dto.RequestUpdateAnnouncementDto;
import com.runcombi.server.domain.announcement.service.AnnouncementService;
import com.runcombi.server.domain.member.dto.GetMemberDetailDto;
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
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@Tag(name = "관리자", description = "관리자 전용 공지/회원/버전 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminRestController {

    private final AnnouncementService announcementService;
    private final MemberService memberService;
    private final VersionService versionService;
    private final AdminStatisticsService adminStatisticsService;

    @Operation(
            summary = "관리자 사용통계 조회",
            description = "Run 테이블의 생성 건수를 집계 단위(`DAY`, `WEEK`, `MONTH`, `YEAR`)와 기간으로 조회합니다.\n" +
                    "기간을 비워 호출하면 단위별 기본 기간으로 조회됩니다. (기본 단위는 WEEK)"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공",
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
                                                "bucketType": "WEEK",
                                                "startDate": "2025-12-01",
                                                "endDate": "2026-02-16",
                                                "totalCount": 245,
                                                "series": [
                                                  {"label": "2025-W49", "count": 18},
                                                  {"label": "2025-W50", "count": 22},
                                                  {"label": "2025-W51", "count": 19}
                                                ]
                                              }
                                            }
                                            """
                            )
                    )),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "관리자 인증 실패")
    })
    @GetMapping("/usage-stats")
    public ApiResponse<UsageStatsResponseDto> getUsageStats(
            @RequestParam(defaultValue = "WEEK") UsageStatsBucketType bucketType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        UsageStatsResponseDto response = adminStatisticsService.getRunUsageStats(bucketType, startDate, endDate);
        return ApiResponse.onSuccess(response);
    }

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
            summary = "관리자 회원 상세 조회",
            description = "관리자가 회원 1명의 상세 정보를 조회합니다. 회원 기본 정보와 함께 최대 2마리의 반려견 정보를 반환합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공",
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
                                                "member": {
                                                  "memberId": 15,
                                                  "provider": "KAKAO",
                                                  "email": "user@runcombi.com",
                                                  "nickname": "달리는몽이",
                                                  "gender": "FEMALE",
                                                  "height": 165.5,
                                                  "weight": 54.2,
                                                  "isActive": "LIVE",
                                                  "profileImgUrl": "https://runcombi.s3.ap-northeast-2.amazonaws.com/member/15xxx",
                                                  "profileImgKey": "member/15xxx",
                                                  "memberTerms": ["TERMS_OF_SERVICE", "PRIVACY_POLICY", "LOCATION_SERVICE_AGREEMENT"]
                                                },
                                                "petList": [
                                                  {
                                                    "petId": 21,
                                                    "name": "몽이",
                                                    "age": 4,
                                                    "weight": 6.2,
                                                    "runStyle": "WALKING",
                                                    "petImageUrl": "https://runcombi.s3.ap-northeast-2.amazonaws.com/pet/21xxx",
                                                    "petImageKey": "pet/21xxx"
                                                  }
                                                ],
                                                "memberStatus": "LIVE"
                                              }
                                            }
                                            """
                            )
                    )),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "회원을 찾을 수 없음 (MEMBER0001)",
                    content = @Content(
                            examples = @ExampleObject(
                                    name = "실패 예시",
                                    value = """
                                            {
                                              "isSuccess": false,
                                              "code": "MEMBER0001",
                                              "message": "사용자가 존재하지 않습니다.",
                                              "result": null
                                            }
                                            """
                            )
                    ))
    })
    @GetMapping("/member/detail")
    public ApiResponse<GetMemberDetailDto> getMemberDetail(
            @RequestParam Long memberId
    ) {
        return ApiResponse.onSuccess(memberService.getMemberDetailForAdmin(memberId));
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
