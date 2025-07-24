package com.runcombi.server.domain.calender.dto;

import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ResponseRunPetDto {
    private Long petId;
    private String name; // 반려 동물 이름
    private String petImageUrl; // 반려 동물 프로필 사진
    private Integer petCal; // 반려 동물 소모 칼로리
}
