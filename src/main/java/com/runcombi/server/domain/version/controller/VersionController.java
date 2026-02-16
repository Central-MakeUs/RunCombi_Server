package com.runcombi.server.domain.version.controller;

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
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Tag(name = "버전", description = "앱 최소 지원 버전 점검 API")
@RestController
@RequestMapping("/version")
@RequiredArgsConstructor
public class VersionController {
    private final VersionService versionService;

    @Operation(
            summary = "앱 버전 점검",
            description = "클라이언트 OS/버전을 기반으로 최소 지원 버전 충족 여부를 판별합니다.\n" +
                    "응답의 `result.updateRequire` 값이 `Y`면 업데이트가 필요하고, `N`이면 현재 버전 사용이 가능합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "버전 점검 성공",
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
                                                "updateRequire": "N"
                                              }
                                            }
                                            """
                            )
                    )),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "요청 값 오류",
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
                    ))
    })
    @PostMapping("/check")
    public ApiResponse<Map<String, String>> versionCheck(
            @RequestBody RequestVersionDto requestVersionDto
    ) {
        Map<String, String> result = versionService.versionCheck(requestVersionDto.getOs(), requestVersionDto.getVersion());

        return ApiResponse.onSuccess(result);
    }
}
