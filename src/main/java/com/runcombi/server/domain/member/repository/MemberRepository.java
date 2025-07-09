package com.runcombi.server.domain.member.repository;

import com.runcombi.server.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
