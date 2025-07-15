package com.runcombi.server.domain.member.dto;

import com.runcombi.server.domain.member.entity.*;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class MemberDto {
    private Long memberId;
    private Provider provider; // kakao / apple
    private String email;   // 이메일
    private String nickname;    // 닉네임
    private Gender gender;  // 성별
    private Double height;  // 키
    private Double weight;  // 몸무게
    private MemberStatus isActive;  // 활동상태
    private String profileImgUrl;
    private String profileImgKey;
    private List<TermType> memberTerms; // 약관 동의
}
