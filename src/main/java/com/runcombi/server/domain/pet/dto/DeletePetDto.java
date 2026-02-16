package com.runcombi.server.domain.pet.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "반려동물 삭제 요청 DTO")
public class DeletePetDto {
    @Schema(description = "삭제 대상 반려동물 ID", example = "21")
    private Long deletePetId;
}
