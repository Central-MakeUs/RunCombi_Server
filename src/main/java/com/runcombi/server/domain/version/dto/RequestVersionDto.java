package com.runcombi.server.domain.version.dto;

import com.runcombi.server.domain.version.entity.OS;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "버전 점검/등록 요청 DTO")
public class RequestVersionDto {
    @Schema(description = "클라이언트 OS", example = "Android")
    private OS os;
    @Schema(description = "앱 버전", example = "1.2.3")
    private String version;
    @Schema(description = "업데이트 상세 내용(관리자 등록 시 사용)", example = "버그 수정 및 안정성 개선")
    private String updateDetail;
}
