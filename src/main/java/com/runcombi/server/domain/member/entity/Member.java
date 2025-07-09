package com.runcombi.server.domain.member.entity;

import com.runcombi.server.domain.base.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Member extends BaseTimeEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    private String email;   // 이메일

    private String nickname;    // 닉네임

    @Enumerated(EnumType.STRING)
    private Role role;  // 회원 등급

    @Enumerated(EnumType.STRING)
    private Gender gender;  // 성별

    private Double height;  // 키

    private Double weight;  // 몸무게

    @Enumerated(EnumType.STRING)
    private Provider provider;  // KAKAO, APPLE

    @Enumerated(EnumType.STRING)
    private MemberStatus isActive;  // 활동상태

    @Enumerated(EnumType.STRING)
    private RunStyle runStyle;  // 산책 스타일

    private String refreshToken;

    private int registerStep; // step : 1(계정 등록만), 2(필요 정보 기입해야 함)

//    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
//    private final List<Pet> pets = new ArrayList<>();

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public String getUsername() {
        return "";
    }
}
