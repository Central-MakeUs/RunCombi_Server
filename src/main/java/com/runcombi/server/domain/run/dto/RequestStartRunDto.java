package com.runcombi.server.domain.run.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class RequestStartRunDto {
    private List<Long> petList;
}
