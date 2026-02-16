package com.runcombi.server.domain.run.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.List;

@Getter
@Schema(description = "산책 종료 시 반려동물 데이터 DTO")
public class RequestEndPetRunDto {
    @Schema(description = "참여 반려동물 목록")
    private List<PetCalDto> petCalList;
}
