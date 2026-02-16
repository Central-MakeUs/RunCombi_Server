package com.runcombi.server.auth.apple.controller;

import com.runcombi.server.auth.apple.dto.AppleLoginRequestDto;
import com.runcombi.server.auth.apple.dto.AppleTokenResponseDto;
import com.runcombi.server.auth.apple.service.AppleLoginService;
import com.runcombi.server.auth.kakao.dto.LoginResponseDTO;
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

import java.util.Map;

@Tag(name = "인증 - Apple", description = "Apple OAuth 기반 로그인 API")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AppleController {
    private final AppleLoginService appleLoginService;

    @Operation(
            summary = "Apple 로그인",
            description = "Apple Authorization Code를 전달받아 Apple 토큰 교환 및 ID 토큰 파싱을 수행한 뒤, 서버 회원 상태를 기반으로 RunCombi 토큰(Access/Refresh)을 발급합니다.\n" +
                    "신규 회원 또는 추가 정보 입력 대상은 `finishRegister=N`, 가입 완료 회원은 `finishRegister=Y`로 반환합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "로그인 성공. 공통 응답의 result에 토큰/회원정보가 포함됩니다.",
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
                                                "memberId": 26,
                                                "email": "apple-user@runcombi.com",
                                                "accessToken": "eyJhbGciOiJIUzI1NiJ9.access",
                                                "refreshToken": "eyJhbGciOiJIUzI1NiJ9.refresh",
                                                "finishRegister": "N"
                                              }
                                            }
                                            """
                            )
                    )),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Authorization Code 또는 Apple 토큰 파싱 오류",
                    content = @Content(
                            examples = @ExampleObject(
                                    name = "실패 예시",
                                    value = """
                                            {
                                              "isSuccess": false,
                                              "code": "APPLE0001",
                                              "message": "APPLE 회원 탈퇴 요청에 실패했습니다.",
                                              "result": null
                                            }
                                            """
                            )
                    ))
    })
    @PostMapping("/apple/login")
    public ApiResponse<LoginResponseDTO> appleLogin(@RequestBody AppleLoginRequestDto appleLoginRequestDto) {
        AppleTokenResponseDto appleTokenResponseDto = appleLoginService.requestTokenToApple(appleLoginRequestDto.getAuthorizationCode());
        Map<String, Object> userInfo = appleLoginService.parseIdToken(appleTokenResponseDto.getIdToken());
        LoginResponseDTO loginResponseDTO = appleLoginService.appleLogin((String) userInfo.get("sub"), (String) userInfo.get("email"), appleTokenResponseDto.getRefreshToken());

        return ApiResponse.onSuccess(loginResponseDTO);
    }
}
