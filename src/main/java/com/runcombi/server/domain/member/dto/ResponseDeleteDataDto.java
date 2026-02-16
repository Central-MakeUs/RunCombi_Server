package com.runcombi.server.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Schema(description = "탈퇴 전 요약 데이터 응답 DTO")
public class ResponseDeleteDataDto {
    @Schema(description = "회원이 등록한 반려동물 이름 목록", example = "[\"몽이\", \"초코\"]")
    List<String> resultPetName;
    @Schema(description = "총 산책 기록 수", example = "42")
    int resultRun;
    @Schema(description = "산책 이미지 수", example = "12")
    int resultRunImage;
}
