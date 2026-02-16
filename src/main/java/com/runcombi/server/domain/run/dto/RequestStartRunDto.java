package com.runcombi.server.domain.run.dto;

import com.runcombi.server.domain.pet.entity.RunStyle;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.List;

@Getter
@Schema(description = "산책 시작 요청 DTO")
public class RequestStartRunDto {
    @Schema(description = "참여 반려동물 ID 목록", example = "[21, 22]")
    private List<Long> petList;
    @Schema(description = "회원 산책 스타일", example = "WALKING")
    private RunStyle memberRunStyle;
}
