package com.runcombi.server.domain.member.entity;

public enum MemberStatus {
    LIVE,       // 활동 회원
    DELETE,     // 탈퇴 회원
    REPORT,     // 신고당한 회원
    DORMANCY,    // 휴면 회원
    PENDING_AGREE,  // 약관동의 대기 회원
    PENDING_MEMBER_DETAIL   // 멤버, 펫 정보 기입 대기 회원
}
