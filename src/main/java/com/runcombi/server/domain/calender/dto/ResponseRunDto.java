package com.runcombi.server.domain.calender.dto;

import com.runcombi.server.domain.pet.entity.RunStyle;
import com.runcombi.server.domain.run.entity.RunEvaluating;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Schema(description = "산책 상세 응답 DTO")
public class ResponseRunDto {
    @Schema(description = "산책 ID", example = "501")
    private Long runId;
    @Schema(description = "회원 닉네임", example = "달리는몽이")
    private String nickname;
    @Schema(description = "회원 프로필 이미지 URL", example = "https://runcombi.s3.ap-northeast-2.amazonaws.com/member/15xxx")
    private String profileImgUrl;
    @Schema(description = "산책 시간(분)", example = "54")
    private Integer runTime; // minute 기준
    @Schema(description = "산책 거리(km)", example = "4.92")
    private Double runDistance; // km 기준
    @Schema(description = "회원 산책 스타일", example = "WALKING")
    private RunStyle memberRunStyle; // 사용자 산책 스타일
    @Schema(description = "회원 소모 칼로리(kcal)", example = "264")
    private Integer memberCal; // 멤버 소모 칼로리
    @Schema(description = "산책 평가", example = "NORMAL")
    private RunEvaluating runEvaluating; // 달리기 평가 (SO_EASY, EASY, NORMAL, HARD, VERY_HARD)
    @Schema(description = "산책 대표 이미지 URL", example = "https://runcombi.s3.ap-northeast-2.amazonaws.com/run/501xxx")
    private String runImageUrl; // 산책 이미지
    @Schema(description = "산책 경로 이미지 URL", example = "https://runcombi.s3.ap-northeast-2.amazonaws.com/route/501xxx")
    private String routeImageUrl; // 산책 경로 이미지
    @Schema(description = "산책 메모", example = "날씨가 좋아서 길게 걸었어요.")
    private String memo; // 메모
    @Schema(description = "산책 등록 시각", example = "2026-02-16T20:11:00")
    private LocalDateTime regDate; // 등록 일자
    @Schema(description = "참여 반려동물 데이터")
    List<ResponseRunPetDto> petData; // 반려 동물 데이터
}
