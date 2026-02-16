package com.runcombi.server.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Schema(description = "관리자 회원 삭제 요청 DTO")
public class RequestMemberIdDto {
    @Schema(description = "삭제 대상 회원 ID", example = "15")
    private Long memberId;
}
