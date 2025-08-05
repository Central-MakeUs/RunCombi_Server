package com.runcombi.server.domain.version.dto;

import com.runcombi.server.domain.version.entity.OS;
import lombok.Getter;

@Getter
public class RequestVersionDto {
    private OS os;
    private String version;
}
