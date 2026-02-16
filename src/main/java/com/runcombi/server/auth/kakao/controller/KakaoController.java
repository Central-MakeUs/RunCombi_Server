package com.runcombi.server.auth.kakao.controller;

import com.runcombi.server.auth.kakao.dto.KakaoLoginRequestDto;
import com.runcombi.server.auth.kakao.dto.LoginResponseDTO;
import com.runcombi.server.auth.kakao.service.KakaoLoginService;
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

@Tag(name = "인증 - 카카오", description = "카카오 OAuth 기반 로그인 API")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class KakaoController {
    private final KakaoLoginService kakaoLoginService;

    @Operation(
            summary = "카카오 로그인",
            description = "카카오 액세스 토큰으로 사용자 정보를 조회하고, 서버의 회원 상태를 확인해 RunCombi 토큰(Access/Refresh)을 발급합니다.\n" +
                    "신규 회원 또는 추가 정보 입력이 필요한 회원은 `finishRegister=N`, 가입 완료 회원은 `finishRegister=Y`로 반환합니다."
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
                                                "memberId": 15,
                                                "email": "user@runcombi.com",
                                                "accessToken": "eyJhbGciOiJIUzI1NiJ9.access",
                                                "refreshToken": "eyJhbGciOiJIUzI1NiJ9.refresh",
                                                "finishRegister": "Y"
                                              }
                                            }
                                            """
                            )
                    )),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "카카오 토큰 오류 (KAKAO0001, KAKAO0002, KAKAO0003)",
                    content = @Content(
                            examples = @ExampleObject(
                                    name = "실패 예시",
                                    value = """
                                            {
                                              "isSuccess": false,
                                              "code": "KAKAO0002",
                                              "message": "유효하지 않은 카카오 인증 토큰입니다.",
                                              "result": null
                                            }
                                            """
                            )
                    ))
    })
    @PostMapping("/kakao/login")
    public ApiResponse<LoginResponseDTO> kakaoLogin(@RequestBody KakaoLoginRequestDto kakaoLoginRequestDto) {
         LoginResponseDTO loginResponse = kakaoLoginService.kakaoLogin(kakaoLoginRequestDto.getKakaoAccessToken());
         return ApiResponse.onSuccess(loginResponse);
    }
}
