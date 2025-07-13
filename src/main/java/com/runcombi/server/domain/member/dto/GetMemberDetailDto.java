package com.runcombi.server.domain.member.dto;

import com.runcombi.server.domain.member.entity.Member;
import com.runcombi.server.domain.member.entity.MemberStatus;
import com.runcombi.server.domain.pet.entity.Pet;
import lombok.*;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class GetMemberDetailDto {
    private Member member;
    private List<Pet> petList;
    private MemberStatus memberStatus;
}
