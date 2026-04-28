package com.example.wardrobeservices.service;

import com.example.wardrobeservices.dto.request.LoginRequest;
import com.example.wardrobeservices.dto.request.RefreshTokenRequest;
import com.example.wardrobeservices.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse login(LoginRequest request);
    AuthResponse refreshToken(RefreshTokenRequest request);
}
