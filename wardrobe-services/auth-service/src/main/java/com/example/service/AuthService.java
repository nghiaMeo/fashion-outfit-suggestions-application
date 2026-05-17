package com.example.service;


import com.example.dto.request.*;
import com.example.dto.response.AuthResponse;

public interface AuthService {
    
    AuthResponse login(LoginRequest request);
    
    AuthResponse refreshToken(RefreshTokenRequest request);
    
    void logout(RefreshTokenRequest request, String accessToken);

    void changePassword(ChangePasswordRequest request);
    void forgotPassword(ForgotPasswordRequest request);
    void resetPassword(ResetPasswordRequest request);
}

