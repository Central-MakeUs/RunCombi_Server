package com.runcombi.server.auth.jwt.controller;

import com.runcombi.server.auth.jwt.dto.ResponseTokenDto;
import com.runcombi.server.domain.member.service.MemberService;
import com.runcombi.server.global.exception.CustomException;
import com.runcombi.server.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.runcombi.server.global.exception.code.CustomErrorList.*;

@Tag(name = "인증 - JWT", description = "JWT 재발급 API")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class JwtController {
    private final MemberService memberService;

    @Operation(
            summary = "Access/Refresh 토큰 재발급",
            description = "요청 헤더의 `RefreshToken` 값을 사용해 토큰 유효성을 검증하고, 새로운 AccessToken/RefreshToken을 재발급합니다.\n" +
                    "헤더 형식: `RefreshToken: Bearer {refreshToken}`",
            parameters = {
                    @Parameter(
                            name = "RefreshToken",
                            in = ParameterIn.HEADER,
                            required = true,
                            description = "Refresh 토큰 헤더",
                            example = "Bearer eyJhbGciOiJIUzI1NiJ9..."
                    )
            }
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "재발급 성공. 공통 응답의 result에 신규 토큰이 포함됩니다.",
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
                                                "refreshToken": "eyJhbGciOiJIUzI1NiJ9.newRefresh",
                                                "accessToken": "eyJhbGciOiJIUzI1NiJ9.newAccess"
                                              }
                                            }
                                            """
                            )
                    )),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "토큰 없음/형식 오류/유효하지 않은 토큰 (TOKEN0001, TOKEN0004)",
                    content = @Content(
                            examples = @ExampleObject(
                                    name = "실패 예시",
                                    value = """
                                            {
                                              "isSuccess": false,
                                              "code": "TOKEN0004",
                                              "message": "유효하지 않은 RefreshToken 입니다.",
                                              "result": null
                                            }
                                            """
                            )
                    ))
    })
    @PostMapping("/refresh")
    public ApiResponse<ResponseTokenDto> refresh(
            @Parameter(hidden = true) HttpServletRequest request
    ) {
        String refreshToken = request.getHeader("RefreshToken");
        if (!StringUtils.hasText(refreshToken)) {
            throw new CustomException(TOKEN_EMPTY);
        }

        if (!refreshToken.startsWith("Bearer ")) {
            throw new CustomException(REFRESH_TOKEN_INVALID);
        }

        ResponseTokenDto responseTokenDto = memberService.regenerateAccessToken(refreshToken.substring(7));
        return ApiResponse.onSuccess(responseTokenDto);
    }
}
