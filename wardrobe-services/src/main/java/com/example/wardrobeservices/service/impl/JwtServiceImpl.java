package com.example.wardrobeservices.service.impl;

import com.example.wardrobeservices.entity.User;
import com.example.wardrobeservices.service.JwtService;
import lombok.RequiredArgsConstructor;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

    private final org.springframework.data.redis.core.StringRedisTemplate redisTemplate;

    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpiration;

    @Override
    public String generateAccessToken(User user) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("role", user.getRole().name());
        extraClaims.put("userId", user.getId().toString());

        return Jwts.builder()
                .claims(extraClaims)
                .subject(user.getEmail())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
                .signWith(getSignInKey())
                .compact();
    }

    @Override
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    @Override
    public boolean isTokenValid(String token, User user) {
        final String email = extractEmail(token);
        // Token hợp lệ khi: email khớp, chưa hết hạn và không nằm trong blacklist
        return (email.equals(user.getEmail())) && !isTokenExpired(token) && !isTokenBlacklisted(token);
    }

    @Override
    public void blacklistToken(String token) {
        // Lấy thời điểm hết hạn của Token
        Date expiration = extractClaim(token, Claims::getExpiration);
        // Tính thời gian còn lại (ms)
        long diff = expiration.getTime() - System.currentTimeMillis();

        if (diff > 0) {
            // Lưu vào Redis với tiền tố blacklist, giá trị là "true", TTL là thời gian còn lại của token
            redisTemplate.opsForValue().set(
                    "jwt:blacklist:" + token,
                    "true",
                    java.time.Duration.ofMillis(diff)
            );
        }
    }

    @Override
    public boolean isTokenBlacklisted(String token) {
        // Kiểm tra xem key này có tồn tại trong Redis không
        return Boolean.TRUE.equals(redisTemplate.hasKey("jwt:blacklist:" + token));
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
