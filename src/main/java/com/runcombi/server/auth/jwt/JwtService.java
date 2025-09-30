package com.runcombi.server.auth.jwt;

import com.runcombi.server.domain.member.entity.Role;
import io.jsonwebtoken.*;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class JwtService {
    @Value("${spring.jwt.secret-key}")
    private String SECRET_KEY;
    private final UserDetailServiceImpl userDetailService;

    private Long accessTokenExpireTime = 1000L * 30; // 30초 테스트
    // private Long accessTokenExpireTime = 1000L * 60 * 60 * 24 * 7; // 7일
    private Long refreshTokenExpireTime = 1000L * 60 * 60 * 24 * 30; // 30일

    public String validateToken(String token) {
        Date now = new Date();

        try {
            String base64EncodedSecretKey = encodeBase64(SECRET_KEY);
            Key key = getKeyByBase64(base64EncodedSecretKey);

            Jws<Claims> claims = Jwts.parser()
                    .setSigningKey(key)
                    .parseClaimsJws(token);
            if(claims.getBody().getExpiration().before(new Date(now.getTime()))) {
                return "expired";
            } else {
                return "live";
            }
        } catch (JwtException e) {
            return "undefined";
        }
    }

    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userDetailService.loadUserByUsername(this.getMemberIdFromJwtToken(token).toString());
        return new UsernamePasswordAuthenticationToken(userDetails, token, userDetails.getAuthorities());
    }

    public Long getMemberIdFromJwtToken(String token) {
        try {
            String base64EncodedSecretKey = encodeBase64(SECRET_KEY);
            Key key = getKeyByBase64(base64EncodedSecretKey);

            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return Long.parseLong(claims.getSubject());
        } catch(Exception e) {
            throw new JwtException(e.getMessage());
        }
    }

    public String encodeBase64(String secretKey) {
        return Encoders.BASE64.encode(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public Key getKeyByBase64(String encodedSecretKey) {
        byte[] byteKey = Decoders.BASE64.decode(encodedSecretKey);
        return Keys.hmacShaKeyFor(byteKey);
    }

    public String createAccessToken(Long memberId, Role role) {
        Date now = new Date();
        String base64EncodedSecretKey = encodeBase64(SECRET_KEY);
        Key key = getKeyByBase64(base64EncodedSecretKey);

        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setIssuedAt(now)
                .setSubject(String.valueOf(memberId))
                .setExpiration(new Date(now.getTime() + accessTokenExpireTime))  // 7일
                .setSubject(String.valueOf(memberId)) // memberId 값 subject 로 설정
                .claim("role", role)
                .signWith(key)
                .compact();
    }

    public String createRefreshToken(Long memberId, Role role) {
        Date now = new Date();
        String base64EncodedSecretKey = encodeBase64(SECRET_KEY);
        Key key = getKeyByBase64(base64EncodedSecretKey);

        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setIssuedAt(now)
                .setSubject(String.valueOf(memberId))
                .setExpiration(new Date(now.getTime() + refreshTokenExpireTime))  // 30일
                .claim("memberId", memberId)
                .claim("role", role)
                .signWith(key)
                .compact();
    }

    // 토큰 유효성 + 만료일자 확인
    public Boolean validateTokenBoolean(String token) {
        Date now = new Date();

        try {
            String base64EncodedSecretKey = encodeBase64(SECRET_KEY);
            Key key = getKeyByBase64(base64EncodedSecretKey);

            Jws<Claims> claims = Jwts.parser()
                    .setSigningKey(key)
                    .parseClaimsJws(token);
            return !claims.getBody().getExpiration().before(new Date(now.getTime()));
        } catch (JwtException e) {
            return false;
        }
    }
}
