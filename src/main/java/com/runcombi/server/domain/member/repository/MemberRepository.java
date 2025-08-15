package com.runcombi.server.domain.member.repository;

import com.runcombi.server.domain.member.entity.Member;
import com.runcombi.server.domain.member.entity.Provider;
import com.runcombi.server.domain.member.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Member findByMemberId(Long memberId);
    Optional<Member> findByEmailAndProvider(String email, Provider provider);

    Optional<Member> findByRefreshToken(String refreshToken);

    Optional<Member> findBySubAndProvider(String sub, Provider provider);

    Optional<Member> findByEmail(String email);

    // Role이 USER인 Member 리스트 반환
    List<Member> findByRole(Role role);
}
