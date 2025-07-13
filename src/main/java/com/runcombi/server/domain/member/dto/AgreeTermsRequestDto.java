package com.runcombi.server.domain.member.dto;

import com.runcombi.server.domain.member.entity.TermType;
import lombok.Getter;

import java.util.List;

@Getter
public class AgreeTermsRequestDto {
    private List<TermType> agreeTermList;
}
