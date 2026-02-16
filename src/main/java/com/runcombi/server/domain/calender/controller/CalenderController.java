package com.runcombi.server.domain.calender.controller;

import com.runcombi.server.domain.calender.dto.*;
import com.runcombi.server.domain.calender.service.CalenderService;
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
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Slf4j
@Tag(name = "캘린더/기록", description = "월/일/상세 조회 및 산책 기록 관리 API")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CalenderController {
    private final CalenderService calenderService;

    @Operation(
            summary = "월별 산책 데이터 조회",
            description = "요청한 연/월의 날짜별 산책 ID 목록과 월 평균 통계(시간/칼로리/거리/주요 산책유형)를 반환합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "월 데이터 조회 성공",
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
                                                "monthData": [
                                                  {"date": "20260215", "runId": [501]},
                                                  {"date": "20260216", "runId": [502, 503]}
                                                ],
                                                "avgTime": 43,
                                                "avgCal": 233,
                                                "avgDistance": 3.41,
                                                "mostRunStyle": "WALKING"
                                              }
                                            }
                                            """
                            )
                    )),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "유효하지 않은 month 값 (CALENDER0001)",
                    content = @Content(
                            examples = @ExampleObject(
                                    name = "실패 예시",
                                    value = """
                                            {
                                              "isSuccess": false,
                                              "code": "CALENDER0001",
                                              "message": "유효하지 않은 month 입니다.",
                                              "result": null
                                            }
                                            """
                            )
                    ))
    })
    @PostMapping("/calender/getMonthData")
    public ApiResponse<ResponseMonthRunDto> getMonthData(
            @Parameter(hidden = true) @AuthenticationPrincipal Member member,
            @RequestBody RequestDateDto requestDateDto
    ) {
        ResponseMonthRunDto monthData = calenderService.getMonthData(member, requestDateDto.getYear(), requestDateDto.getMonth());
        return ApiResponse.onSuccess(monthData);
    }

    @Operation(
            summary = "일별 산책 목록 조회",
            description = "요청한 날짜의 산책 목록을 반환합니다.\n" +
                    "각 항목에는 산책 시간/거리, 대표 이미지, 등록시각이 포함됩니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "일 데이터 조회 성공",
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
                                                  "runId": 501,
                                                  "runTime": 54,
                                                  "runDistance": 4.92,
                                                  "runImageUrl": "https://runcombi.s3.ap-northeast-2.amazonaws.com/run/501xx",
                                                  "regDate": "2026-02-16T20:11:00"
                                                }
                                              ]
                                            }
                                            """
                            )
                    )),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "유효하지 않은 month 값 (CALENDER0001)",
                    content = @Content(
                            examples = @ExampleObject(
                                    name = "실패 예시",
                                    value = """
                                            {
                                              "isSuccess": false,
                                              "code": "CALENDER0001",
                                              "message": "유효하지 않은 month 입니다.",
                                              "result": null
                                            }
                                            """
                            )
                    ))
    })
    @PostMapping("/calender/getDayData")
    public ApiResponse<List<ResponseDayRunDto>> getDayData(
            @Parameter(hidden = true) @AuthenticationPrincipal Member member,
            @RequestBody RequestDateDto requestDateDto
    ) {
        List<ResponseDayRunDto> dayDataList = calenderService.getDayData(member, requestDateDto.getYear(), requestDateDto.getMonth(), requestDateDto.getDay());
        return ApiResponse.onSuccess(dayDataList);
    }

    @Operation(
            summary = "산책 상세 조회",
            description = "특정 산책의 상세 정보를 반환합니다.\n" +
                    "회원 정보, 산책 지표, 평가, 메모, 경로 이미지, 반려동물별 소모 칼로리를 포함합니다."
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
                                                "runId": 501,
                                                "nickname": "달리는몽이",
                                                "profileImgUrl": "https://runcombi.s3.ap-northeast-2.amazonaws.com/member/15xx",
                                                "runTime": 54,
                                                "runDistance": 4.92,
                                                "memberRunStyle": "WALKING",
                                                "memberCal": 264,
                                                "runEvaluating": "NORMAL",
                                                "runImageUrl": "https://runcombi.s3.ap-northeast-2.amazonaws.com/run/501xx",
                                                "routeImageUrl": "https://runcombi.s3.ap-northeast-2.amazonaws.com/route/501xx",
                                                "memo": "날씨가 좋아서 길게 걸었어요.",
                                                "regDate": "2026-02-16T20:11:00",
                                                "petData": [
                                                  {
                                                    "petId": 21,
                                                    "name": "몽이",
                                                    "petImageUrl": "https://runcombi.s3.ap-northeast-2.amazonaws.com/pet/21xx",
                                                    "petCal": 97
                                                  }
                                                ]
                                              }
                                            }
                                            """
                            )
                    )),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "유효하지 않은 산책 번호 또는 회원 불일치 (RUN0003, RUN0004)",
                    content = @Content(
                            examples = @ExampleObject(
                                    name = "실패 예시",
                                    value = """
                                            {
                                              "isSuccess": false,
                                              "code": "RUN0004",
                                              "message": "회원의 산책 정보가 아닙니다.",
                                              "result": null
                                            }
                                            """
                            )
                    ))
    })
    @PostMapping("/calender/getRunData")
    public ApiResponse<ResponseRunDto> getRunData(
            @Parameter(hidden = true) @AuthenticationPrincipal Member member,
            @RequestBody RequestRunIdDto requestRunIdDto
    ) {
        ResponseRunDto runData = calenderService.getRunData(member, requestRunIdDto.getRunId());
        return ApiResponse.onSuccess(runData);
    }

    @Operation(
            summary = "산책 평가 등록",
            description = "산책 강도/난이도 평가(`SO_EASY`, `EASY`, `NORMAL`, `HARD`, `VERY_HARD`)를 저장합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "평가 등록 성공",
                    content = @Content(
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "성공 예시",
                                    value = """
                                            {
                                              "isSuccess": true,
                                              "code": "STATUS200",
                                              "message": "요청에 성공하셨습니다.",
                                              "result": "산책 평가 등록에 성공하였습니다."
                                            }
                                            """
                            )
                    )),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "유효하지 않은 산책 번호 또는 회원 불일치 (RUN0003, RUN0004)",
                    content = @Content(
                            examples = @ExampleObject(
                                    name = "실패 예시",
                                    value = """
                                            {
                                              "isSuccess": false,
                                              "code": "RUN0003",
                                              "message": "유효하지 않은 산책 번호입니다.",
                                              "result": null
                                            }
                                            """
                            )
                    ))
    })
    @PostMapping("/calender/setRunEvaluating")
    public ApiResponse<String> setRunEvaluating(
            @Parameter(hidden = true) @AuthenticationPrincipal Member member,
            @RequestBody RequestRunEvaluatingDto runEvaluatingDto
    ) {
        calenderService.setRunEvaluating(member, runEvaluatingDto.getRunId(), runEvaluatingDto.getRunEvaluating());
        return ApiResponse.onSuccess("산책 평가 등록에 성공하였습니다.");
    }

    @Operation(
            summary = "산책 대표 이미지 변경",
            description = "특정 산책의 대표 이미지를 업로드/교체합니다.\n" +
                    "요청은 `multipart/form-data`이며 `runId`, `runImage` 파트를 사용합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "이미지 변경 성공",
                    content = @Content(
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "성공 예시",
                                    value = """
                                            {
                                              "isSuccess": true,
                                              "code": "STATUS200",
                                              "message": "요청에 성공하셨습니다.",
                                              "result": "산책 이미지 변경에 성공하였습니다."
                                            }
                                            """
                            )
                    )),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "유효하지 않은 산책 번호 또는 회원 불일치, 이미지 형식 오류 (RUN0003, RUN0004, FILE0001)",
                    content = @Content(
                            examples = @ExampleObject(
                                    name = "실패 예시",
                                    value = """
                                            {
                                              "isSuccess": false,
                                              "code": "FILE0001",
                                              "message": "이미지 확장자 파일이 아닙니다.",
                                              "result": null
                                            }
                                            """
                            )
                    ))
    })
    @PostMapping(value = "/calender/setRunImage", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<String> setRunImage(
            @Parameter(hidden = true) @AuthenticationPrincipal Member member,
            @Parameter(description = "산책 ID", required = true)
            @RequestPart Long runId,
            @Parameter(description = "산책 이미지 파일", required = true)
            @RequestPart(value = "runImage") MultipartFile runImage
    ) {
        calenderService.setRunImage(member, runId, runImage);

        return ApiResponse.onSuccess("산책 이미지 변경에 성공하였습니다.");
    }

    @Operation(
            summary = "산책 메모 수정",
            description = "특정 산책의 메모 내용을 수정합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "메모 수정 성공",
                    content = @Content(
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "성공 예시",
                                    value = """
                                            {
                                              "isSuccess": true,
                                              "code": "STATUS200",
                                              "message": "요청에 성공하셨습니다.",
                                              "result": "산책 메모 변경에 성공하였습니다."
                                            }
                                            """
                            )
                    )),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "유효하지 않은 산책 번호 또는 회원 불일치 (RUN0003, RUN0004)",
                    content = @Content(
                            examples = @ExampleObject(
                                    name = "실패 예시",
                                    value = """
                                            {
                                              "isSuccess": false,
                                              "code": "RUN0004",
                                              "message": "회원의 산책 정보가 아닙니다.",
                                              "result": null
                                            }
                                            """
                            )
                    ))
    })
    @PostMapping("/calender/updateMemo")
    public ApiResponse<String> updateMemo(
            @Parameter(hidden = true) @AuthenticationPrincipal Member member,
            @RequestBody RequestRunMemoDto requestRunMemoDto
    ) {
        calenderService.updateMemo(member, requestRunMemoDto.getRunId(), requestRunMemoDto.getMemo());

        return ApiResponse.onSuccess("산책 메모 변경에 성공하였습니다.");
    }

    @Operation(
            summary = "산책 기록 삭제",
            description = "특정 산책 기록을 삭제합니다."
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
                                              "result": "산책 정보 삭제에 성공하였습니다."
                                            }
                                            """
                            )
                    )),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "유효하지 않은 산책 번호 또는 회원 불일치 (RUN0003, RUN0004)",
                    content = @Content(
                            examples = @ExampleObject(
                                    name = "실패 예시",
                                    value = """
                                            {
                                              "isSuccess": false,
                                              "code": "RUN0003",
                                              "message": "유효하지 않은 산책 번호입니다.",
                                              "result": null
                                            }
                                            """
                            )
                    ))
    })
    @PostMapping("/calender/deleteRun")
    public ApiResponse<String> deleteRun(
            @Parameter(hidden = true) @AuthenticationPrincipal Member member,
            @RequestBody RequestRunIdDto requestRunIdDto
    ) {
        calenderService.deleteRun(member, requestRunIdDto.getRunId());

        return ApiResponse.onSuccess("산책 정보 삭제에 성공하였습니다.");
    }

    @Operation(
            summary = "산책 기록 수동 등록",
            description = "과거 산책 기록을 수동으로 등록합니다.\n" +
                    "회원 산책 스타일/시간/거리/시작시각 및 참여 반려동물 목록을 입력하면 새 `runId`를 반환합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "산책 기록 등록 성공",
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
                                                "runId": 712
                                              }
                                            }
                                            """
                            )
                    )),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "회원 소유 반려동물 불일치 (PET0002)",
                    content = @Content(
                            examples = @ExampleObject(
                                    name = "실패 예시",
                                    value = """
                                            {
                                              "isSuccess": false,
                                              "code": "PET0002",
                                              "message": "회원님의 반려 동물이 아닙니다.",
                                              "result": null
                                            }
                                            """
                            )
                    ))
    })
    @PostMapping("/calender/addRun")
    public ApiResponse<Map<String, Long>> addRun(
        @Parameter(hidden = true) @AuthenticationPrincipal Member member,
        @RequestBody RequestAddRunDto addRunData
    ) {
        Map<String, Long> result = calenderService.addRun(member, addRunData);

        return ApiResponse.onSuccess(result);
    }

    @Operation(
            summary = "산책 상세 정보 수정",
            description = "기존 산책의 시작시각/산책스타일/시간/거리 정보를 수정합니다.\n" +
                    "거리 변경 시 회원/반려동물 칼로리도 재계산됩니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "산책 정보 수정 성공",
                    content = @Content(
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "성공 예시",
                                    value = """
                                            {
                                              "isSuccess": true,
                                              "code": "STATUS200",
                                              "message": "요청에 성공하셨습니다.",
                                              "result": "산책 정보 변경에 성공하였습니다."
                                            }
                                            """
                            )
                    )),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "유효하지 않은 산책 번호 또는 회원 불일치 (RUN0003, RUN0004)",
                    content = @Content(
                            examples = @ExampleObject(
                                    name = "실패 예시",
                                    value = """
                                            {
                                              "isSuccess": false,
                                              "code": "RUN0004",
                                              "message": "회원의 산책 정보가 아닙니다.",
                                              "result": null
                                            }
                                            """
                            )
                    ))
    })
    @PostMapping("/calender/updateRunDetail")
    public ApiResponse<String> updateRunDetail(
            @Parameter(hidden = true) @AuthenticationPrincipal Member member,
            @RequestBody RequestUpdateRunDetailDto requestUpdqtRunDetailDto
    ) {
        calenderService.updateRunDetail(member, requestUpdqtRunDetailDto);

        return ApiResponse.onSuccess("산책 정보 변경에 성공하였습니다.");
    }
}
