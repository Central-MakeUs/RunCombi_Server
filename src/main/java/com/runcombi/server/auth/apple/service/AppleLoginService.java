package com.runcombi.server.auth.apple.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.runcombi.server.auth.apple.dto.AppleTokenResponseDto;
import com.runcombi.server.auth.jwt.JwtService;
import com.runcombi.server.auth.kakao.dto.LoginResponseDTO;
import com.runcombi.server.domain.member.entity.Member;
import com.runcombi.server.domain.member.entity.MemberStatus;
import com.runcombi.server.domain.member.entity.Provider;
import com.runcombi.server.domain.member.entity.Role;
import com.runcombi.server.domain.member.repository.MemberRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppleLoginService {
    private final MemberRepository memberRepository;
    private final JwtService jwtService;

    @Value("${spring.apple.client-id}")
    private String clientId;
    @Value("${spring.apple.team-id}")
    private String teamId;
    @Value("${spring.apple.key-id}")
    private String keyId;
    @Value("${spring.apple.private-key}")
    private String privateKey;

    // 1. 토큰 요청
    public AppleTokenResponseDto requestTokenToApple(String code) {
        String clientSecret = generateClientSecret();

        String url = "https://appleid.apple.com/auth/token";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "authorization_code");
        form.add("client_id", clientId);
        form.add("client_secret", clientSecret);
        form.add("code", code);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(form, headers);

        try {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<AppleTokenResponseDto> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    AppleTokenResponseDto.class
            );
            return response.getBody();
        } catch (HttpStatusCodeException e) {
            System.err.println("애플 OAuth 실패 : " + e.getResponseBodyAsString());
            throw e;
        }
    }

    // 2. 애플 아이디토큰 파싱 (sub, email 등)
    public Map<String, Object> parseIdToken(String idToken) {
        String[] parts = idToken.split("\\.");
        if (parts.length < 2) throw new IllegalArgumentException("Invalid JWT");
        String payload = parts[1];
        byte[] decodedBytes = Base64.getUrlDecoder().decode(payload);
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(decodedBytes, Map.class);
        } catch (Exception e) {
            throw new RuntimeException("토큰 파싱 실패", e);
        }
    }

    // 3. client_secret JWT 발급
    private String generateClientSecret() {
        try {
            PrivateKey generatePrivateKey = parsePrivateKeyPem(privateKey);

            long now = System.currentTimeMillis();
            // claims 세팅
            String jwt = Jwts.builder()
                    .setHeaderParam("kid", keyId)
                    .setHeaderParam("alg", "ES256")
                    .setIssuer(teamId)
                    .setAudience("https://appleid.apple.com")
                    .setSubject(clientId)
                    .setIssuedAt(new Date(now))
                    .setExpiration(new Date(now + 60 * 60 * 1000L)) // 1시간
                    .signWith(generatePrivateKey, SignatureAlgorithm.ES256)
                    .compact();
            return jwt;
        } catch (Exception e) {
            throw new RuntimeException("client_secret 생성 실패", e);
        }
    }

    public static PrivateKey parsePrivateKeyPem(String privateKeyPem) throws Exception {
        byte[] pkcs8EncodedBytes = Base64.getDecoder().decode(privateKeyPem);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(pkcs8EncodedBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("EC");
        return keyFactory.generatePrivate(keySpec);
    }

    @Transactional
    public LoginResponseDTO appleLogin(String sub, String email, String appleRefreshToken) {
        Optional<Member> optionalMember = memberRepository.findBySubAndProvider(sub, Provider.APPLE);
        if(optionalMember.isEmpty()) {
            // 1. 회원 없으면 빌드 + 저장
            Member newMember = Member.builder()
                    .sub(sub)
                    .email(email)
                    .role(Role.USER)
                    .provider(Provider.APPLE)
                    .appleRefreshToken(appleRefreshToken)
                    .isActive(MemberStatus.PENDING_AGREE)
                    .build();
            Member savedMember = memberRepository.save(newMember);

            String accessToken = jwtService.createAccessToken(savedMember.getMemberId(), savedMember.getRole());
            String refreshToken = jwtService.createRefreshToken(savedMember.getMemberId(), savedMember.getRole());

            savedMember.updateRefreshToken(refreshToken);

            return LoginResponseDTO
                    .builder()
                    .email(email)
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .finishRegister("N")
                    .build();
        } else {
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

    public boolean revokeAppleToken(String refreshToken) {
        String clientSecret = generateClientSecret();

        String url = "https://appleid.apple.com/auth/revoke";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("client_id", clientId);
        form.add("client_secret", clientSecret);
        form.add("token", refreshToken);
        form.add("token_type_hint", "refresh_token");

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(form, headers);

        try {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    String.class
            );
            // Apple의 revoke API는 성공해도 body가 empty일 수 있어서, 200 OK면 성공 처리
            return response.getStatusCode() == HttpStatus.OK;
        } catch (HttpStatusCodeException e) {
            log.error("애플 회원 탈퇴(토큰폐기) 실패: " + e.getResponseBodyAsString(), e);
            return false;
        }
    }
}
