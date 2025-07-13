package com.runcombi.server.domain.pet.entity;

import com.runcombi.server.domain.base.BaseTimeEntity;
import com.runcombi.server.domain.member.entity.Member;
import com.runcombi.server.domain.pet.dto.SetPetDetailDto;
import com.runcombi.server.global.s3.dto.S3ImageReturnDto;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pet extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long petId;

    @Setter
    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    private String name;
    private int age; // 나이
    private Double weight;  // 몸무게

    @Enumerated(EnumType.STRING)
    private RunStyle runStyle;  // 산책 스타일

    private String petImageUrl;
    private String petImageKey;

    public void setPetDetail(SetPetDetailDto petDetailDto) {
        this.name = petDetailDto.getName();
        this.age = petDetailDto.getAge();
        this.weight = petDetailDto.getWeight();
        this.runStyle = petDetailDto.getRunStyle();
    }

    public void setPetImage(S3ImageReturnDto s3ImageReturnDto) {
        this.petImageUrl = s3ImageReturnDto.getImageUrl();
        this.petImageKey = s3ImageReturnDto.getImageKey();
    }

}
