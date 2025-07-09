package com.runcombi.server.auth.jwt;

import com.runcombi.server.domain.member.repository.MemberRepository;
import com.runcombi.server.global.exception.ExceptionHandler;
import com.runcombi.server.global.exception.code.CustomErrorList;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailServiceImpl implements UserDetailsService {
    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String memberId) throws UsernameNotFoundException {
        UserDetails result = memberRepository.findById(Long.parseLong(memberId))
                .orElseThrow(() -> new ExceptionHandler(CustomErrorList.MEMBER_NOT_FOUND));
        return result;
    }
}
