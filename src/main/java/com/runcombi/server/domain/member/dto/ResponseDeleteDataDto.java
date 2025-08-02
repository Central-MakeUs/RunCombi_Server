package com.runcombi.server.domain.member.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ResponseDeleteDataDto {
    List<String> resultPetName;
    int resultRun;
    int resultRunImage;
}
