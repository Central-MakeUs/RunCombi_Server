package com.runcombi.server.domain.member.repository;

import com.runcombi.server.domain.member.entity.Member;
import com.runcombi.server.domain.member.entity.MemberTerm;
import com.runcombi.server.domain.member.entity.Provider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberTermRepository extends JpaRepository<MemberTerm, Long> {
    List<MemberTerm> findByMember(Member member);
}
