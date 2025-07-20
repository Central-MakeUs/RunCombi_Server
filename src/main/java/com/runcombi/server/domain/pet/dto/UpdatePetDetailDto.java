package com.runcombi.server.domain.pet.dto;

import com.runcombi.server.domain.pet.entity.RunStyle;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UpdatePetDetailDto {
    private Long petId;
    private String name;
    private int age;
    private Double weight;
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
