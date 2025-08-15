package com.runcombi.server.admin.security;

import com.runcombi.server.domain.member.entity.Member;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CustomAdminDetails implements UserDetails {
    private final Member member;

    public CustomAdminDetails(Member member) {
        this.member = member;
    }

    // UserDetails method들 구현, member의 필드를 사용하여 반환
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + member.getRole().toString()));
    }
    @Override
    public String getPassword() { return member.getNickname(); }
    @Override
    public String getUsername() { return member.getEmail(); }
}
