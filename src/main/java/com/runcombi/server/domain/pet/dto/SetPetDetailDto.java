package com.runcombi.server.domain.pet.dto;

import com.runcombi.server.domain.pet.entity.RunStyle;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "반려동물 등록 DTO")
public class SetPetDetailDto {
    @Schema(description = "반려동물 이름", example = "몽이")
    private String name;
    @Schema(description = "반려동물 나이(살)", example = "4")
    private int age;
    @Schema(description = "반려동물 몸무게(kg)", example = "6.2")
    private Double weight;
    @Schema(description = "반려동물 산책 스타일", example = "WALKING")
    private RunStyle runStyle;

    @Override
    public String toString() {
        return "name : " + name +
                ", age : " + age +
                ", weight : " + weight +
                ", runStyle : " + runStyle;
    }
}
