package com.runcombi.server.domain.member.dto;

import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class RequestSuggestionDto {
    private String sggMsg;
}
