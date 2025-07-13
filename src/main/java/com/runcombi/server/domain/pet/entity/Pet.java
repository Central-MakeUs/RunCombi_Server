package com.runcombi.server.domain.pet.entity;

import com.runcombi.server.domain.base.BaseTimeEntity;
import com.runcombi.server.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pet extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long petId;

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
}
