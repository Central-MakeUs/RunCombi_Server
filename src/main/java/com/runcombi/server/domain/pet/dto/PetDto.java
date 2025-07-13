package com.runcombi.server.domain.pet.dto;

import com.runcombi.server.domain.pet.entity.RunStyle;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PetDto {
    private Long petId;
    private String name;
    private int age; // 나이
    private Double weight;  // 몸무게
    private RunStyle runStyle;  // 산책 스타일
    private String petImageUrl;
    private String petImageKey;
}
