package com.runcombi.server.domain.version.entity;

import com.runcombi.server.domain.base.BaseTimeEntity;
import com.runcombi.server.domain.version.dto.RequestVersionDto;
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
public class Version extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long versionId;

    @Enumerated(EnumType.STRING)
    private OS os;

    private String version;

    private String updateDetail;    // 변경 사항

    public void updateVersion(RequestVersionDto requestVersionDto) {
        this.os = requestVersionDto.getOs();
        this.version = requestVersionDto.getVersion();
    }
}
