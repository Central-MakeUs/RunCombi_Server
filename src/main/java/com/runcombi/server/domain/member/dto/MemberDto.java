package com.runcombi.server.domain.member.dto;

import com.runcombi.server.domain.member.entity.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
@Schema(description = "회원 기본 정보 DTO")
public class MemberDto {
    @Schema(description = "회원 ID", example = "15")
    private Long memberId;
    @Schema(description = "가입 소셜 제공자", example = "KAKAO")
    private Provider provider; // kakao / apple
    @Schema(description = "이메일", example = "user@runcombi.com")
    private String email;   // 이메일
    @Schema(description = "닉네임", example = "달리는몽이")
    private String nickname;    // 닉네임
    @Schema(description = "성별", example = "FEMALE")
    private Gender gender;  // 성별
    @Schema(description = "키(cm)", example = "165.5")
    private Double height;  // 키
    @Schema(description = "몸무게(kg)", example = "54.2")
    private Double weight;  // 몸무게
    @Schema(description = "회원 상태", example = "LIVE")
    private MemberStatus isActive;  // 활동상태
    @Schema(description = "프로필 이미지 URL", example = "https://runcombi.s3.ap-northeast-2.amazonaws.com/member/15xxx")
    private String profileImgUrl;
    @Schema(description = "프로필 이미지 S3 Key", example = "member/15xxx")
    private String profileImgKey;
    @Schema(description = "동의한 약관 목록")
    private List<TermType> memberTerms; // 약관 동의
}
