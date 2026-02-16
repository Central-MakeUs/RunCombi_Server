package com.runcombi.server.domain.member.dto;

import com.runcombi.server.domain.member.entity.MemberStatus;
import com.runcombi.server.domain.pet.dto.PetDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Schema(description = "회원 상세 조회 응답 DTO")
public class GetMemberDetailDto {
    @Schema(description = "회원 정보")
    private MemberDto member;
    @Schema(description = "반려동물 목록")
    private List<PetDto> petList;
    @Schema(description = "회원 상태", example = "LIVE")
    private MemberStatus memberStatus;
}
