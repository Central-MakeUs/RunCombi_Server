package com.runcombi.server.domain.run.entity;

import com.runcombi.server.domain.base.BaseTimeEntity;
import com.runcombi.server.domain.member.entity.Member;
import com.runcombi.server.domain.pet.entity.RunStyle;
import com.runcombi.server.global.s3.dto.S3ImageReturnDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Run  extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long runId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Enumerated(EnumType.STRING)
    private RunStyle memberRunStyle; // 사용자 산책 스타일

    private Integer memberCal; // 멤버 소모 칼로리

    @OneToMany(mappedBy = "run", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<RunPet> runPets = new ArrayList<>();

    private Integer runTime; // minute 기준

    private Double runDistance; // km 기준

    @Enumerated(EnumType.STRING)
    private RunEvaluating runEvaluating; // 달리기 평가 (SO_EASY, EASY, NORMAL, HARD, VERY_HARD)

    private String runImageUrl;

    private String runImageKey;

    private String routeImageUrl;

    private String routeImageKey;

    @Column(columnDefinition = "TEXT")
    private String memo; // 메모

    public void setRunPets(RunPet runPet) {
        this.runPets.add(runPet);
        runPet.setRun(this);
    }

    public void updateRun(Integer memberCal, Integer runTime, Double runDistance, RunEvaluating runEvaluating, String memo) {
        this.memberCal = memberCal;
        this.runTime = runTime;
        this.runDistance = runDistance;
        this.runEvaluating = runEvaluating;
        this.memo = memo;
    }

    public void setRunImage(S3ImageReturnDto runImageReturnDto) {
        this.runImageUrl = runImageReturnDto.getImageUrl();
        this.runImageKey = runImageReturnDto.getImageKey();
    }

    public void setRouteImage(S3ImageReturnDto routeImageReturnDto) {
        this.routeImageUrl = routeImageReturnDto.getImageUrl();
        this.routeImageKey = routeImageReturnDto.getImageKey();
    }
}
