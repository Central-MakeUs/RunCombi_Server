package com.runcombi.server.domain.member.controller;

import com.runcombi.server.domain.member.dto.*;
import com.runcombi.server.domain.member.entity.Member;
import com.runcombi.server.domain.member.entity.TermType;
import com.runcombi.server.domain.member.service.MemberService;
import com.runcombi.server.domain.pet.dto.SetPetDetailDto;
import com.runcombi.server.domain.pet.entity.Pet;
import com.runcombi.server.domain.pet.service.PetService;
import com.runcombi.server.global.response.ApiResponse;
import com.runcombi.server.global.s3.dto.S3ImageReturnDto;
import com.runcombi.server.global.s3.service.S3Service;
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

@Slf4j
@Tag(name = "회원", description = "회원 기본정보/약관/탈퇴/피드백 API")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;
    private final PetService petService;

    @Operation(
            summary = "회원+첫 반려동물 최초 등록",
            description = "회원 프로필과 첫 반려동물 정보를 한 번에 등록합니다.\n" +
                    "요청은 `multipart/form-data`이며, JSON 파트(`memberDetail`, `pet`)와 이미지 파트(`memberImage`, `petImage`)를 함께 보낼 수 있습니다.\n" +
                    "등록 성공 시 회원 상태는 가입 완료 상태로 전환됩니다."
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
                                              "result": "정보 등록에 성공하셨습니다."
                                            }
                                            """
                            )
                    )),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "필수값 누락(FIELD0001), 이미지 형식 오류(FILE0001)",
                    content = @Content(
                            examples = @ExampleObject(
                                    name = "실패 예시",
                                    value = """
                                            {
                                              "isSuccess": false,
                                              "code": "FIELD0001",
                                              "message": "필수 입력 필드가 비어있습니다.",
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
                                              "code": "TOKEN0002",
                                              "message": "유효하지 않은 AccessToken 입니다.",
                                              "result": null
                                            }
                                            """
                            )
                    ))
    })
    @PostMapping(value = "/member/setMemberDetail", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<String> setMemberDetail(
            @Parameter(hidden = true) @AuthenticationPrincipal Member member,
            @Parameter(description = "회원 상세정보(JSON)", required = true)
            @RequestPart("memberDetail") SetMemberDetailDto memberDetail,
            @Parameter(description = "회원 프로필 이미지 파일(선택)", required = false)
            @RequestPart(value = "memberImage", required = false) MultipartFile memberImage,
            @Parameter(description = "첫 반려동물 상세정보(JSON)", required = true)
            @RequestPart("pet") SetPetDetailDto  petDetail,
            @Parameter(description = "첫 반려동물 프로필 이미지 파일(선택)", required = false)
            @RequestPart(value = "petImage", required = false) MultipartFile petImage
            ) {
        memberService.setMemberPetDetail(member, memberDetail, memberImage, petDetail, petImage);
        return ApiResponse.onSuccess("정보 등록에 성공하셨습니다.");
    }

    @Operation(
            summary = "회원 상세 조회",
            description = "회원 기본정보와 반려동물 목록을 함께 조회합니다.\n" +
                    "응답 `result.member`에는 회원 정보, `result.petList`에는 반려동물 리스트가 포함됩니다."
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
                                                  "profileImgUrl": "https://runcombi.s3.ap-northeast-2.amazonaws.com/member/15xx",
                                                  "profileImgKey": "member/15xx",
                                                  "memberTerms": ["TERMS_OF_SERVICE", "PRIVACY_POLICY", "LOCATION_SERVICE_AGREEMENT"]
                                                },
                                                "petList": [
                                                  {
                                                    "petId": 21,
                                                    "name": "몽이",
                                                    "age": 4,
                                                    "weight": 6.2,
                                                    "runStyle": "WALKING",
                                                    "petImageUrl": "https://runcombi.s3.ap-northeast-2.amazonaws.com/pet/21xx",
                                                    "petImageKey": "pet/21xx"
                                                  }
                                                ],
                                                "memberStatus": "LIVE"
                                              }
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
    @PostMapping("/member/getMemberDetail")
    public ApiResponse<GetMemberDetailDto> getMemberDetail(
            @Parameter(hidden = true) @AuthenticationPrincipal Member member
    ) {
        List<Pet> petList = petService.getPetList(member);
        GetMemberDetailDto getMemberDetailDto = memberService.getMemberPetDetail(member, petList);
        return ApiResponse.onSuccess(getMemberDetailDto);
    }

    @Operation(
            summary = "회원 약관 동의 저장",
            description = "회원이 동의한 약관 목록을 저장합니다.\n" +
                    "필수 약관 3종이 모두 동의되면 상태값이 다음 가입 단계로 전환됩니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "저장 성공",
                    content = @Content(
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "성공 예시",
                                    value = """
                                            {
                                              "isSuccess": true,
                                              "code": "STATUS200",
                                              "message": "요청에 성공하셨습니다.",
                                              "result": "약관 동의 저장에 성공하였습니다."
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
                                              "code": "TOKEN0003",
                                              "message": "만료된 AccessToken 입니다.",
                                              "result": null
                                            }
                                            """
                            )
                    ))
    })
    @PostMapping("/member/setMemberTerms")
    public ApiResponse<String> setMemberTerms(
            @Parameter(hidden = true) @AuthenticationPrincipal Member member,
            @RequestBody AgreeTermsRequestDto agreeTermsList
    ) {
        memberService.setMemberTerms(agreeTermsList.getAgreeTermList(), member);

        return ApiResponse.onSuccess("약관 동의 저장에 성공하였습니다.");
    }

    @Operation(
            summary = "회원 정보 수정",
            description = "회원 닉네임/성별/신체정보를 수정하고, 선택적으로 프로필 이미지를 교체합니다.\n" +
                    "이미지를 새로 전달하면 기존 프로필 이미지는 삭제 후 교체됩니다."
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
                                              "result": "정보 수정에 성공하였습니다."
                                            }
                                            """
                            )
                    )),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "이미지 형식 오류(FILE0001)",
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
                    )),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "토큰 오류 (TOKEN0001~TOKEN0003)",
                    content = @Content(
                            examples = @ExampleObject(
                                    name = "토큰 실패 예시",
                                    value = """
                                            {
                                              "isSuccess": false,
                                              "code": "TOKEN0002",
                                              "message": "유효하지 않은 AccessToken 입니다.",
                                              "result": null
                                            }
                                            """
                            )
                    ))
    })
    @PostMapping(value = "/member/updateMemberDetail", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<String> updateMemberDetail(
            @Parameter(hidden = true) @AuthenticationPrincipal Member member,
            @Parameter(description = "수정할 회원 상세정보(JSON)", required = true)
            @RequestPart(value = "updateMemberDetail") SetMemberDetailDto updateMemberDto,
            @Parameter(description = "회원 프로필 이미지 파일(선택)", required = false)
            @RequestPart(value = "memberImage", required = false) MultipartFile memberImage
    ) {
        memberService.updateMemberDetail(member, updateMemberDto, memberImage);

        return ApiResponse.onSuccess("정보 수정에 성공하였습니다.");
    }

    @Operation(
            summary = "회원 탈퇴",
            description = "회원 계정을 삭제합니다.\n" +
                    "외부 소셜 연동 해제(카카오/애플)와 함께 회원/반려동물/산책 이미지 및 산책 데이터 정리 로직이 수행됩니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "탈퇴 성공",
                    content = @Content(
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "성공 예시",
                                    value = """
                                            {
                                              "isSuccess": true,
                                              "code": "STATUS200",
                                              "message": "요청에 성공하셨습니다.",
                                              "result": "회원 탈퇴에 성공하였습니다."
                                            }
                                            """
                            )
                    )),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "토큰 오류 또는 외부 연동 해제 실패 (TOKEN0001~TOKEN0003, KAKAO0004, APPLE0001)",
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
    @PostMapping("/member/deleteAccount")
    public ApiResponse<String> deleteAccount(
            @Parameter(hidden = true) @AuthenticationPrincipal Member member
    ) {
        memberService.deleteAccount(member);

        return ApiResponse.onSuccess("회원 탈퇴에 성공하였습니다.");
    }

    @Operation(
            summary = "탈퇴 전 데이터 요약 조회",
            description = "탈퇴 안내 화면에서 사용할 요약 데이터를 조회합니다.\n" +
                    "반려동물 이름 목록, 총 산책 수, 산책 이미지 수를 반환합니다."
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
                                                "resultPetName": ["몽이", "초코"],
                                                "resultRun": 42,
                                                "resultRunImage": 12
                                              }
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
    @PostMapping("/member/getDeleteData")
    public ApiResponse<ResponseDeleteDataDto> getDeleteData(
            @Parameter(hidden = true) @AuthenticationPrincipal Member member
    ) {
        ResponseDeleteDataDto responseDeleteDataDto = memberService.getDeleteData(member);

        return ApiResponse.onSuccess(responseDeleteDataDto);
    }

    @Operation(
            summary = "개선 제안 등록",
            description = "회원이 작성한 개선 제안 내용을 서버에 전달합니다.\n" +
                    "내부적으로 Discord 웹훅을 사용해 운영 채널로 전달됩니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "제안 등록 성공",
                    content = @Content(
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "성공 예시",
                                    value = """
                                            {
                                              "isSuccess": true,
                                              "code": "STATUS200",
                                              "message": "요청에 성공하셨습니다.",
                                              "result": "제안 등록에 성공했습니다."
                                            }
                                            """
                            )
                    )),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "제안 전송 실패 (DISCORD0001)",
                    content = @Content(
                            examples = @ExampleObject(
                                    name = "실패 예시",
                                    value = """
                                            {
                                              "isSuccess": false,
                                              "code": "DISCORD0001",
                                              "message": "개선제안에 실패하였습니다.",
                                              "result": null
                                            }
                                            """
                            )
                    ))
    })
    @PostMapping("/member/suggestion")
    public ApiResponse<String> suggestion(
            @Parameter(hidden = true) @AuthenticationPrincipal Member member,
            @RequestBody RequestSuggestionDto requestSuggestionDto
    ) {
        memberService.suggestion(member, requestSuggestionDto.getSggMsg());

        return ApiResponse.onSuccess("제안 등록에 성공했습니다.");
    }

    @Operation(
            summary = "탈퇴 사유 등록",
            description = "회원이 선택한 탈퇴 사유 목록을 서버에 전달합니다.\n" +
                    "내부적으로 Discord 웹훅을 사용해 운영 채널로 전달됩니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "탈퇴 사유 등록 성공",
                    content = @Content(
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "성공 예시",
                                    value = """
                                            {
                                              "isSuccess": true,
                                              "code": "STATUS200",
                                              "message": "요청에 성공하셨습니다.",
                                              "result": "탈퇴 사유 등록에 성공했습니다."
                                            }
                                            """
                            )
                    )),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "탈퇴 사유 전송 실패 (DISCORD0002)",
                    content = @Content(
                            examples = @ExampleObject(
                                    name = "실패 예시",
                                    value = """
                                            {
                                              "isSuccess": false,
                                              "code": "DISCORD0002",
                                              "message": "회원 탈퇴 사유 등록에 실패하였습니다.",
                                              "result": null
                                            }
                                            """
                            )
                    ))
    })
    @PostMapping("/member/leaveReason")
    public ApiResponse<String> leaveReason(
            @Parameter(hidden = true) @AuthenticationPrincipal Member member,
            @RequestBody RequestLeaveReasonDto requestLeaveReasonDto
    ) {
        memberService.leaveReason(member, requestLeaveReasonDto.getReason());

        return ApiResponse.onSuccess("탈퇴 사유 등록에 성공했습니다.");
    }
}
