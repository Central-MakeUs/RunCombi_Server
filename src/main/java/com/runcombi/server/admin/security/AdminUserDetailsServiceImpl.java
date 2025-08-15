package com.runcombi.server.admin.security;

import com.runcombi.server.domain.member.entity.Member;
import com.runcombi.server.domain.member.entity.Role;
import com.runcombi.server.domain.member.repository.MemberRepository;
import com.runcombi.server.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static com.runcombi.server.global.exception.code.CustomErrorList.*;

@Service("adminUserDetailsServiceImpl")
@RequiredArgsConstructor
public class AdminUserDetailsServiceImpl implements UserDetailsService {
    private final MemberRepository memberRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findByEmail(username).orElseThrow(() -> new CustomException(ADMIN_NOT_FOUND));
        if(member.getRole() != Role.ADMIN) throw new CustomException(ADMIN_NOT_FOUND);
        return new CustomAdminDetails(member);
    }
}
