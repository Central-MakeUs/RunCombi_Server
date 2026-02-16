package com.runcombi.server.domain.pet.dto;

import com.runcombi.server.domain.pet.entity.RunStyle;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@Schema(description = "반려동물 정보 응답 DTO")
public class PetDto {
    @Schema(description = "반려동물 ID", example = "21")
    private Long petId;
    @Schema(description = "이름", example = "몽이")
    private String name;
    @Schema(description = "나이(살)", example = "4")
    private int age; // 나이
    @Schema(description = "몸무게(kg)", example = "6.2")
    private Double weight;  // 몸무게
    @Schema(description = "산책 스타일", example = "WALKING")
    private RunStyle runStyle;  // 산책 스타일
    @Schema(description = "프로필 이미지 URL", example = "https://runcombi.s3.ap-northeast-2.amazonaws.com/pet/21xxx")
    private String petImageUrl;
    @Schema(description = "프로필 이미지 S3 Key", example = "pet/21xxx")
    private String petImageKey;
}
