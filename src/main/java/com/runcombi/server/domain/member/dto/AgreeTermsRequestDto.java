package com.runcombi.server.domain.member.dto;

import com.runcombi.server.domain.member.entity.TermType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.List;

@Getter
@Schema(description = "약관 동의 요청 DTO")
public class AgreeTermsRequestDto {
    @Schema(
            description = "동의한 약관 타입 목록",
            example = "[\"TERMS_OF_SERVICE\", \"PRIVACY_POLICY\", \"LOCATION_SERVICE_AGREEMENT\"]"
    )
    private List<TermType> agreeTermList;
}
