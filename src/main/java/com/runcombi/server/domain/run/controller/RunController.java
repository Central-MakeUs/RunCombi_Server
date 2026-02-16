package com.runcombi.server.domain.run.controller;

import com.runcombi.server.domain.member.entity.Member;
import com.runcombi.server.domain.run.dto.*;
import com.runcombi.server.domain.run.service.RunService;
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

@Slf4j
@Tag(name = "실시간 산책", description = "산책 시작/중간저장/종료 API")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RunController {
    private final RunService runService;

    @Operation(
            summary = "산책 시작",
            description = "산책을 시작하고 `runId`를 생성합니다.\n" +
                    "요청한 반려동물 ID가 회원 소유인지 검증하며, 응답으로 최초 산책 여부(`isFirstRun`)와 이번 달 n번째 산책(`nthRun`)을 반환합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "산책 시작 성공",
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
                                                "isFirstRun": "N",
                                                "nthRun": 7
                                              }
                                            }
                                            """
                            )
                    )),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "반려동물 미선택(RUN0001), 회원 소유 불일치(PET0002)",
                    content = @Content(
                            examples = @ExampleObject(
                                    name = "실패 예시",
                                    value = """
                                            {
                                              "isSuccess": false,
                                              "code": "RUN0001",
                                              "message": "반려 동물이 선택되지 않았습니다.",
                                              "result": null
                                            }
                                            """
                            )
                    ))
    })
    @PostMapping("/run/startRun")
    public ApiResponse<ResponseStartRunDto> startRun(
            @Parameter(hidden = true) @AuthenticationPrincipal Member member,
            @RequestBody RequestStartRunDto requestStartRunDto
    ) {
        ResponseStartRunDto responseStartRunDto = runService.startRun(member, requestStartRunDto.getPetList(), requestStartRunDto.getMemberRunStyle());

        return ApiResponse.onSuccess(responseStartRunDto);
    }

    @Operation(
            summary = "산책 중간 저장",
            description = "산책 진행 중 거리/시간 데이터를 중간 저장합니다.\n" +
                    "회원 소유 산책인지 검증한 뒤, 회원/반려동물 칼로리 계산값을 함께 업데이트합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "중간 저장 성공",
                    content = @Content(
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "성공 예시",
                                    value = """
                                            {
                                              "isSuccess": true,
                                              "code": "STATUS200",
                                              "message": "요청에 성공하셨습니다.",
                                              "result": "산책 정보 중간 저장에 성공했습니다."
                                            }
                                            """
                            )
                    )),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "유효하지 않은 산책 번호(RUN0003)",
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
    @PostMapping("/run/midRunUpdate")
    public ApiResponse<String> midProgressUpdate(
            @Parameter(hidden = true) @AuthenticationPrincipal Member member,
            @RequestBody RequestMidRunUpdateDto requestMidRunUpdateDto
    ) {
        runService.midRunUpdate(member, requestMidRunUpdateDto);

        return ApiResponse.onSuccess("산책 정보 중간 저장에 성공했습니다.");
    }

    @Operation(
            summary = "산책 종료 저장",
            description = "산책 종료 시 최종 시간/거리, 반려동물 참여 정보, 경로 이미지를 저장합니다.\n" +
                    "요청은 `multipart/form-data`이며 `memberRunData`(JSON), `petRunData`(JSON), `routeImage`(파일) 파트를 사용합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "산책 종료 저장 성공",
                    content = @Content(
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "성공 예시",
                                    value = """
                                            {
                                              "isSuccess": true,
                                              "code": "STATUS200",
                                              "message": "요청에 성공하셨습니다.",
                                              "result": "산책 정보 업데이트에 성공했습니다."
                                            }
                                            """
                            )
                    )),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "유효하지 않은 산책 번호(RUN0003), 회원 소유 불일치(PET0002), 이미지 형식 오류(FILE0001)",
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
    @PostMapping(value = "/run/endRun", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<String> endRun(
            @Parameter(hidden = true) @AuthenticationPrincipal Member member,
            @Parameter(description = "회원 산책 최종 데이터(JSON)", required = true)
            @RequestPart(value = "memberRunData") RequestEndMemberRunDto memberRunData,
            @Parameter(description = "반려동물 참여 데이터(JSON)", required = true)
            @RequestPart(value = "petRunData") RequestEndPetRunDto petRunDataList,
            @Parameter(description = "산책 경로 이미지 파일", required = true)
            @RequestPart(value = "routeImage") MultipartFile routeImage
    ) {
        runService.endRun(member, memberRunData, petRunDataList, routeImage);

        return ApiResponse.onSuccess("산책 정보 업데이트에 성공했습니다.");
    }
}
