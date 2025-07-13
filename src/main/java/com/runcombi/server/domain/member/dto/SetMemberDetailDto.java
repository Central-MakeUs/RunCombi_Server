package com.runcombi.server.domain.member.dto;

import com.runcombi.server.domain.member.entity.Gender;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Getter
@NonNull
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SetMemberDetailDto {
    private String nickname;
    private Gender gender;
    private Double height;
    private Double weight;

    @Override
    public String toString() {
        return "nickname : " + nickname +
                ", gender : " + gender +
                ", height : " + height +
                ", weight : " + weight;
    }
}
