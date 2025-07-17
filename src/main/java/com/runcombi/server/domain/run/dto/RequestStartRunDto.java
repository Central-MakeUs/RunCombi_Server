package com.runcombi.server.domain.run.dto;

import com.runcombi.server.domain.pet.entity.RunStyle;
import lombok.Getter;

import java.util.List;

@Getter
public class RequestStartRunDto {
    private List<Long> petList;
    private RunStyle memberRunStyle;
}
