package com.runcombi.server.domain.run.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "산책 참여 반려동물 DTO")
public class PetCalDto {
    @Schema(description = "반려동물 ID", example = "21")
    private Long petId;
}
