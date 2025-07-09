package com.runcombi.server.domain.member.entity;

public enum MemberStatus {
    LIVE,       // 활동 회원
    DELETE,     // 탈퇴 회원
    REPORT,     // 신고당한 회원
    DORMANCY,    // 휴면 회원
    PENDING     // 추가정보 대기 회원
}
