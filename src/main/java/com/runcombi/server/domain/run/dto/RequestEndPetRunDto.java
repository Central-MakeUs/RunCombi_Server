package com.runcombi.server.domain.run.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class RequestEndPetRunDto {
    private List<PetCalDto> petCalList;
}
