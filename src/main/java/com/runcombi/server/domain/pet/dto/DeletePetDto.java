package com.runcombi.server.domain.pet.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DeletePetDto {
    private Long deletePetId;
}
