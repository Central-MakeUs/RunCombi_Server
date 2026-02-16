package com.runcombi.server.domain.calender.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Schema(description = "산책 상세 내 반려동물 데이터 DTO")
public class ResponseRunPetDto {
    @Schema(description = "반려동물 ID", example = "21")
    private Long petId;
    @Schema(description = "반려동물 이름", example = "몽이")
    private String name; // 반려 동물 이름
    @Schema(description = "반려동물 이미지 URL", example = "https://runcombi.s3.ap-northeast-2.amazonaws.com/pet/21xxx")
    private String petImageUrl; // 반려 동물 프로필 사진
    @Schema(description = "반려동물 소모 칼로리(kcal)", example = "97")
    private Integer petCal; // 반려 동물 소모 칼로리
}
