package com.runcombi.server.domain.member.dto;

import com.runcombi.server.domain.member.entity.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Getter
@NonNull
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "회원 상세 등록/수정 DTO")
public class SetMemberDetailDto {
    @Schema(description = "닉네임", example = "달리는몽이")
    private String nickname;
    @Schema(description = "성별", example = "FEMALE")
    private Gender gender;
    @Schema(description = "키(cm)", example = "165.5")
    private Double height;
    @Schema(description = "몸무게(kg)", example = "54.2")
    private Double weight;

    @Override
    public String toString() {
        return "nickname : " + nickname +
                ", gender : " + gender +
                ", height : " + height +
                ", weight : " + weight;
    }
}
