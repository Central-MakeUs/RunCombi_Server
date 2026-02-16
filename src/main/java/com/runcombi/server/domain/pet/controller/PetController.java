package com.runcombi.server.domain.pet.controller;

import com.runcombi.server.domain.member.dto.SetMemberDetailDto;
import com.runcombi.server.domain.member.entity.Member;
import com.runcombi.server.domain.pet.dto.DeletePetDto;
import com.runcombi.server.domain.pet.dto.SetPetDetailDto;
import com.runcombi.server.domain.pet.dto.UpdatePetDetailDto;
import com.runcombi.server.domain.pet.service.PetService;
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
@Tag(name = "반려동물", description = "반려동물 추가/수정/삭제 API")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PetController {
    private final PetService petService;

    @Operation(
            summary = "반려동물 추가",
            description = "회원의 반려동물을 추가 등록합니다.\n" +
                    "요청은 `multipart/form-data`이며 JSON 파트(`pet`)와 이미지 파트(`petImage`, 선택)를 사용할 수 있습니다.\n" +
                    "최대 등록 가능 수(2마리)를 초과하면 오류가 발생합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "추가 성공",
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
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "최대 반려동물 수 초과(PET0001), 이미지 형식 오류(FILE0001)",
                    content = @Content(
                            examples = @ExampleObject(
                                    name = "실패 예시",
                                    value = """
                                            {
                                              "isSuccess": false,
                                              "code": "PET0001",
                                              "message": "최대 반려 동물 수를 초과하였습니다.",
                                              "result": null
                                            }
                                            """
                            )
                    ))
    })
    @PostMapping(value = "/pet/addPet", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<String> addPet(
            @Parameter(hidden = true) @AuthenticationPrincipal Member member,
            @Parameter(description = "반려동물 상세정보(JSON)", required = true)
            @RequestPart("pet") SetPetDetailDto petDetail,
            @Parameter(description = "반려동물 프로필 이미지 파일(선택)", required = false)
            @RequestPart(value = "petImage", required = false) MultipartFile petImage
    ) {
        petService.setMemberPetDetail(member, petDetail, petImage);
        return ApiResponse.onSuccess("정보 등록에 성공하셨습니다.");
    }

    @Operation(
            summary = "반려동물 정보 수정",
            description = "반려동물 기본 정보(이름/나이/몸무게/산책스타일)를 수정하고, 선택적으로 이미지를 교체합니다."
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
                                              "result": "정보 수정에 성공하셨습니다."
                                            }
                                            """
                            )
                    )),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "유효하지 않은 반려동물(PET0003), 회원 소유 불일치(PET0002), 이미지 형식 오류(FILE0001)",
                    content = @Content(
                            examples = @ExampleObject(
                                    name = "실패 예시",
                                    value = """
                                            {
                                              "isSuccess": false,
                                              "code": "PET0003",
                                              "message": "유효하지 않은 반려 동물입니다.",
                                              "result": null
                                            }
                                            """
                            )
                    ))
    })
    @PostMapping(value = "/pet/updatePetDetail", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<String> updatePetDetail(
            @Parameter(hidden = true) @AuthenticationPrincipal Member member,
            @Parameter(description = "수정할 반려동물 상세정보(JSON)", required = true)
            @RequestPart("updatePetDetail") UpdatePetDetailDto updatePetDetail,
            @Parameter(description = "반려동물 프로필 이미지 파일(선택)", required = false)
            @RequestPart(value = "petImage", required = false) MultipartFile petImage
    ) {
        petService.updatePetDetail(member, updatePetDetail, petImage);
        return ApiResponse.onSuccess("정보 수정에 성공하셨습니다.");
    }

    @Operation(
            summary = "반려동물 삭제",
            description = "특정 반려동물을 삭제합니다.\n" +
                    "최소 1마리는 유지되어야 하므로 마지막 1마리는 삭제할 수 없습니다."
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
                                              "result": "삭제에 성공하셨습니다."
                                            }
                                            """
                            )
                    )),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "최소 반려동물 수 제한(PET0004), 유효하지 않은 반려동물(PET0003), 회원 소유 불일치(PET0002)",
                    content = @Content(
                            examples = @ExampleObject(
                                    name = "실패 예시",
                                    value = """
                                            {
                                              "isSuccess": false,
                                              "code": "PET0004",
                                              "message": "최소 반려 동물 수는 1마리 입니다.",
                                              "result": null
                                            }
                                            """
                            )
                    ))
    })
    @PostMapping("/pet/deletePet")
    public ApiResponse<String> deletePet(
            @Parameter(hidden = true) @AuthenticationPrincipal Member member,
            @RequestBody DeletePetDto deletePetRequest
    ) {
        petService.deletePet(member, deletePetRequest.getDeletePetId());
        return ApiResponse.onSuccess("삭제에 성공하셨습니다.");
    }
}
