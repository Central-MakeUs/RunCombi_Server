package com.runcombi.server.domain.member.entity;

import com.runcombi.server.domain.base.BaseTimeEntity;
import com.runcombi.server.domain.member.dto.SetMemberDetailDto;
import com.runcombi.server.domain.pet.entity.Pet;
import com.runcombi.server.global.s3.dto.S3ImageReturnDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
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

    private String refreshToken;

    private String profileImgUrl;

    private String profileImgKey;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Pet> pets = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<MemberTerm> memberTerms; // 약관 동의

    // 기타 필드 및 메서드
    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void addMemberTerm(MemberTerm memberTerm) {
        this.memberTerms.add(memberTerm);
    }

    public void addPet(Pet pet) {
        this.pets.add(pet);
        pet.setMember(this);
    }

    public void deletePet(Pet pet) {
        this.pets.remove(pet);
    }

    public void updateIsActive(MemberStatus memberStatus) {
        this.isActive = memberStatus;
    }

    public void setMemberDetail(SetMemberDetailDto memberDetailDto) {
        this.nickname = memberDetailDto.getNickname();
        this.gender = memberDetailDto.getGender();
        this.height = memberDetailDto.getHeight();
        this.weight = memberDetailDto.getWeight();
    }

    public void setMemberImage(S3ImageReturnDto s3ImageReturnDto) {
        this.profileImgUrl = s3ImageReturnDto.getImageUrl();
        this.profileImgKey = s3ImageReturnDto.getImageKey();
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
