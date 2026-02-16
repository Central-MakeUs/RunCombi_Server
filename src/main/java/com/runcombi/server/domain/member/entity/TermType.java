package com.runcombi.server.domain.member.entity;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "약관 타입")
public enum TermType {
    TERMS_OF_SERVICE,           // 이용 약관 동의
    PRIVACY_POLICY,             // 개인정보 처리방침 동의
    LOCATION_SERVICE_AGREEMENT  // 위치기반 서비스 이용약관 동의
}
