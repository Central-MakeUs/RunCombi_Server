package com.runcombi.server.admin.init;

import com.runcombi.server.admin.security.AdminPasswordEncoder;
import com.runcombi.server.domain.member.entity.Member;
import com.runcombi.server.domain.member.entity.Role;
import com.runcombi.server.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AdminAccountInitializer implements CommandLineRunner {

    private final MemberRepository memberRepository;
    private final AdminPasswordEncoder passwordEncoder;

    @Value("${spring.admin.admin-email}")
    private String adminEmail;

    @Value("${spring.admin.admin-password}")
    private String adminPassword;

    @Override
    public void run(String... args) throws Exception {
        Optional<Member> admin = memberRepository.findByEmail(adminEmail);

        // 이미 관리자 존재하는지 체크
        if (admin.isEmpty()) {
            Member newAdmin = Member.builder()
                    .email(adminEmail)
                    .nickname(passwordEncoder.passwordEncoder().encode(adminPassword))
                    .role(Role.ADMIN)
                    .build();
            memberRepository.save(newAdmin);
        }
    }
}
