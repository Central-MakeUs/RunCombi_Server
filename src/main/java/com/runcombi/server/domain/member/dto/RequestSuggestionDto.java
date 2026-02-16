package com.runcombi.server.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Schema(description = "개선 제안 요청 DTO")
public class RequestSuggestionDto {
    @Schema(description = "개선 제안 내용", example = "산책 종료 화면에 공유 기능이 있으면 좋겠어요.")
    private String sggMsg;
}
