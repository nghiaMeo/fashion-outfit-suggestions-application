package com.example.auth.service;

import com.example.user.entity.User;

public interface JwtService {
    
    String generateAccessToken(User user);
    
    String extractEmail(String token);
    
    String extractUsername(String token); // Thường trả về email (Subject)
    
    String extractUserId(String token); // Trích xuất UserId từ Extra Claims
    
    boolean isTokenValid(String token, User user);
    
    boolean isTokenValid(String token);

    void blacklistToken(String token);

    boolean isTokenBlacklisted(String token);
}
