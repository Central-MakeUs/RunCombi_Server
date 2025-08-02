package com.runcombi.server.auth.kakao.service;

import com.runcombi.server.auth.jwt.JwtService;
import com.runcombi.server.auth.kakao.dto.LoginResponseDTO;
import com.runcombi.server.domain.member.entity.Member;
import com.runcombi.server.domain.member.entity.MemberStatus;
import com.runcombi.server.domain.member.entity.Provider;
import com.runcombi.server.domain.member.entity.Role;
import com.runcombi.server.domain.member.repository.MemberRepository;
import com.runcombi.server.global.exception.CustomException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Optional;

import static com.runcombi.server.global.exception.code.CustomErrorList.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class KakaoLoginService {
    private final MemberRepository memberRepository;
    private final JwtService jwtService;

    @Value("${spring.kakao.admin-key}")
    private String adminKey;

    @Transactional
    public LoginResponseDTO kakaoLogin(String kakaoAccessToken) {
        if(kakaoAccessToken == null) {
            // 요청에 토큰 값이 빈 경우
            log.error("kakao token empty ::::: {}", kakaoAccessToken);
            throw new CustomException(KAKAO_TOKEN_EMPTY);
        }
        String userEmail = "";
        Long kakaoUserId = null;

        String requestUrl = "https://kapi.kakao.com/v2/user/me";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + kakaoAccessToken);
        HttpEntity<Void> request = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    requestUrl,
                    HttpMethod.GET,
                    request,
                    Map.class
            );

            Map<String, Object> body = response.getBody();
            log.info("전달받은 body ::::: {}", body);
            Map<String, Object> kakaoAccount = (Map<String, Object>) body.get("kakao_account");
            if (kakaoAccount == null) {
                // 유효하지 않은 카카오 토큰인 경우
                log.error("invalid kakao token(kakaoAcount == null) ::::: {}", kakaoAccessToken);
                throw new CustomException(KAKAO_TOKEN_INVALID);
            }
            kakaoUserId = (Long) body.get("id");
            userEmail = (String) kakaoAccount.get("email");
        } catch(ExpiredJwtException e) {
            // 카카오 토큰이 만료된 경우
            log.error("expired kakao token ::::: {}", kakaoAccessToken);
            throw new CustomException(KAKAO_TOKEN_EXPIRED);
        } catch(JwtException e) {
            // 유효하지 않은 카카오 토큰인 경우
            log.error("invalid kakao token(JwtException) ::::: {}", kakaoAccessToken);
            throw new CustomException(KAKAO_TOKEN_INVALID);
        } catch(HttpClientErrorException e) {
            // 유효하지 않은 카카오 토큰인 경우
            log.error("invalid kakao token(HttpClientErrorException) ::::: {}", kakaoAccessToken);
            log.error("응답코드 ::::: {}", e.getStatusCode());
            log.error("응답바디 ::::: {}", e.getResponseBodyAsString());
            throw new CustomException(KAKAO_TOKEN_INVALID);
        }

        if(userEmail.isEmpty()) {
            // 유저 이메일 정보를 추출하지 못한 경우 - 유효한 토큰이 아님
            log.error("invalid kakao token ::::: {}", kakaoAccessToken);
            throw new CustomException(KAKAO_TOKEN_INVALID);
        }

        String finalUserEmail = userEmail;
        Optional<Member> optionalMember = memberRepository.findByEmailAndProvider(userEmail, Provider.KAKAO);
        if(optionalMember.isEmpty()) {
            // 2. 회원 없으면 빌드 + 저장
            Member newMember = Member.builder()
                    .email(finalUserEmail)
                    .kakaoUserId(kakaoUserId)
                    .role(Role.USER)
                    .provider(Provider.KAKAO)
                    .isActive(MemberStatus.PENDING_AGREE)
                    .build();
            Member savedMember = memberRepository.save(newMember);

            String accessToken = jwtService.createAccessToken(savedMember.getMemberId(), savedMember.getRole());
            String refreshToken = jwtService.createRefreshToken(savedMember.getMemberId(), savedMember.getRole());
            System.out.println(accessToken);
            System.out.println(refreshToken);

            savedMember.updateRefreshToken(refreshToken);

            return LoginResponseDTO
                    .builder()
                    .email(userEmail)
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .finishRegister("N")
                    .build();
        }else {
            Member member = optionalMember.get();
            MemberStatus memberStatus = member.getIsActive();

            String accessToken = jwtService.createAccessToken(member.getMemberId(), member.getRole());
            String refreshToken = jwtService.createRefreshToken(member.getMemberId(), member.getRole());

            // 추가 정보 기입이 필요한 회원일때 응답 (Member 의 isActive 값이 PENDING_AGREE 또는 PENDING_MEMBER_DETAIL 일 경우 응답에 isRegistered("N") 을 담아 리턴
            if(memberStatus == MemberStatus.PENDING_AGREE ||
                    memberStatus == MemberStatus.PENDING_MEMBER_DETAIL) {
                member.updateRefreshToken(refreshToken);

                return LoginResponseDTO
                        .builder()
                        .email(member.getEmail())
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .finishRegister("N")
                        .build();
            }else {
                // 회원가입이 모두 끝난 회원인 경우
                member.updateRefreshToken(refreshToken);
                
                // 회원가입이 모두 완료된 회원 - Access 토큰, Refresh 토큰 발급
                return LoginResponseDTO
                        .builder()
                        .email(member.getEmail())
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .finishRegister("Y")
                        .build();
            }
        }
    }

    public boolean unlinkKakaoAccount(Long kakaoUserId) {
        String url = "https://kapi.kakao.com/v1/user/unlink";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", "KakaoAK " + adminKey);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("target_id_type", "user_id");
        params.add("target_id", String.valueOf(kakaoUserId));   // Long 타입이면 String.valueOf 사용

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(params, headers);

        try {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.exchange(
                    url, HttpMethod.POST, entity, String.class
            );
            // 카카오 unlink 성공
            return response.getStatusCode() == HttpStatus.OK;
        } catch (Exception e) {
            // 카카오 unlink 실패
            log.error("카카오 회원 UNLINK 실패: " + e.getMessage());
            return false;
        }
    }
}
