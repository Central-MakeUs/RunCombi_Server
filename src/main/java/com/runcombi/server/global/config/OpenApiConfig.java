package com.runcombi.server.global.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "RunCombi Server API 문서",
                version = "v1",
                description = "RunCombi 서버 API 명세입니다.\n\n" +
                        "모든 API 응답은 공통 포맷(`isSuccess`, `code`, `message`, `result`)을 사용합니다.\n" +
                        "- 성공: `isSuccess=true`, `code=STATUS200`, `message=요청에 성공하셨습니다.`\n" +
                        "- 실패: `isSuccess=false`, `code=<오류코드>`, `message=<오류메시지>`, `result=null`"
        ),
        servers = {
                @Server(url = "/", description = "기본 서버")
        }
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER,
        description = "인증이 필요한 API는 `Authorization: Bearer {AccessToken}` 형식으로 전달합니다."
)
public class OpenApiConfig {
}
