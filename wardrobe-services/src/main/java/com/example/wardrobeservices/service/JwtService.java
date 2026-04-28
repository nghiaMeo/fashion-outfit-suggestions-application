package com.example.wardrobeservices.service;

import com.example.wardrobeservices.entity.User;

public interface JwtService {
    String generateAccessToken(User user);
    String extractEmail(String token);
    boolean isTokenValid(String token, User user);
}
