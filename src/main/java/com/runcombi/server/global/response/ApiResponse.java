package com.runcombi.server.global.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.runcombi.server.global.exception.code.CustomSuccessList;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonPropertyOrder({"isSuccess", "code", "message", "result"})
@Schema(description = "공통 API 응답 포맷")
public class ApiResponse<T> {
    @Schema(description = "요청 성공 여부", example = "true")
    @JsonProperty("isSuccess")
    private final Boolean isSuccess;

    @Schema(description = "응답 코드 (성공: STATUS200, 실패: 도메인별 오류 코드)", example = "STATUS200")
    private final String code;

    @Schema(description = "응답 메시지", example = "요청에 성공하셨습니다.")
    private final String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "실제 응답 데이터. 실패 시 null")
    private T result;

    public static <T> ApiResponse<T> onFailure(String code, String message, T result) {
        return new ApiResponse<>(false, code, message, result);
    }

    public static <T> ApiResponse<T> onSuccess(T result) {
        return new ApiResponse<>(true, CustomSuccessList._OK.getCode(), CustomSuccessList._OK.getMessage(), result);
    }
}
