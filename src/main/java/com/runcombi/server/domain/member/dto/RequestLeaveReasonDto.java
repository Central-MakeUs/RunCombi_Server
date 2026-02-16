package com.runcombi.server.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Schema(description = "탈퇴 사유 요청 DTO")
public class RequestLeaveReasonDto {
    @Schema(
            description = "탈퇴 사유 목록(복수 선택 가능)",
            example = "[\"사용 빈도가 낮아요\", \"다른 서비스를 이용할게요\"]"
    )
    private List<String> reason;
}
