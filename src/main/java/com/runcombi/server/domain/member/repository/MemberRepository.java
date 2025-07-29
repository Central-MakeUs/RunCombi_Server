package com.runcombi.server.domain.member.repository;

import com.runcombi.server.domain.member.entity.Member;
import com.runcombi.server.domain.member.entity.Provider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Member findByMemberId(Long memberId);
    Optional<Member> findByEmailAndProvider(String email, Provider provider);

    Optional<Member> findByRefreshToken(String refreshToken);

    Optional<Member> findBySubAndProvider(String sub, Provider provider);
}
