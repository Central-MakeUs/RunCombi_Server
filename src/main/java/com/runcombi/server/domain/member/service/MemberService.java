package com.runcombi.server.domain.member.service;

import com.runcombi.server.domain.member.entity.Member;
import com.runcombi.server.domain.member.entity.MemberStatus;
import com.runcombi.server.domain.member.entity.MemberTerm;
import com.runcombi.server.domain.member.entity.TermType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {
    @Transactional
    public void setMemberTerms(List<TermType> agreeTermsList, Member member) {
        List<MemberTerm> originMemberTerms = member.getMemberTerms();

        for(TermType newTermType: agreeTermsList) {
            // 기존에 중복된 동의항목이 있다면 true, 없다면 false
            boolean isDuplicate = originMemberTerms.stream()
                    .anyMatch(memberTerm -> memberTerm.getTermType() == newTermType);

            // 기존 동의항목과 일치하지 않는 새로운 동의항목이라면
            if(!isDuplicate) {
                MemberTerm newMemberTerm = MemberTerm
                        .builder()
                        .member(member)
                        .termType(newTermType)
                        .build();
                member.addMemberTerm(newMemberTerm);
            }
        }

        // 필수 동의 여부
        boolean defaultTermsCheck = checkDefaultTerms(member);

        // 필수 동의 3개를 모두 채우고 멤버의 MemberStatus 가 PENDING_AGREE 상태라면 PENDING_MEMBER_DETAIL 상태로 변경
        if(defaultTermsCheck && member.getIsActive() == MemberStatus.PENDING_AGREE) {
            member.updateIsActive(MemberStatus.PENDING_MEMBER_DETAIL);
        }
    }

    public boolean checkDefaultTerms(Member member) {
        List<TermType> defaultTerms = List.of(
                TermType.TERMS_OF_SERVICE,
                TermType.PRIVACY_POLICY,
                TermType.LOCATION_SERVICE_AGREEMENT
        );

        // 멤버가 동의한 약관의 TermType 집합으로 변환
        Set<TermType> agreedTermTypes = member.getMemberTerms().stream()
                .map(MemberTerm::getTermType)
                .collect(Collectors.toSet());

        // 기본 약관 모두 동의했는지 검사
        return agreedTermTypes.containsAll(defaultTerms);
    }
}
