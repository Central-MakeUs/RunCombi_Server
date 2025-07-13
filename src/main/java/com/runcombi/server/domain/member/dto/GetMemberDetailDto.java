package com.runcombi.server.domain.member.dto;

import com.runcombi.server.domain.member.entity.MemberStatus;
import com.runcombi.server.domain.pet.dto.PetDto;
import lombok.*;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class GetMemberDetailDto {
    private MemberDto member;
    private List<PetDto> petList;
    private MemberStatus memberStatus;
}
