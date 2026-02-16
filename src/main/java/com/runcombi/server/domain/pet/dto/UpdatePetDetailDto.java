package com.runcombi.server.domain.pet.dto;

import com.runcombi.server.domain.pet.entity.RunStyle;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "반려동물 수정 DTO")
public class UpdatePetDetailDto {
    @Schema(description = "수정 대상 반려동물 ID", example = "21")
    private Long petId;
    @Schema(description = "반려동물 이름", example = "몽이")
    private String name;
    @Schema(description = "반려동물 나이(살)", example = "5")
    private int age;
    @Schema(description = "반려동물 몸무게(kg)", example = "6.5")
    private Double weight;
    @Schema(description = "반려동물 산책 스타일", example = "RUNNING")
    private RunStyle runStyle;

    @Override
    public String toString() {
        return "memberId : " + petId +
                ", name : " + name +
                ", age : " + age +
                ", weight : " + weight +
                ", runStyle : " + runStyle;
    }
}
