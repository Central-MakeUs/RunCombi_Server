package com.runcombi.server.domain.run.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResponseStartRunDto {
    private Long runId;
}
