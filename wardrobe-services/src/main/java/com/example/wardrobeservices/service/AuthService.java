package com.example.wardrobeservices.service;

import com.example.wardrobeservices.dto.request.ChangePasswordRequest;
import com.example.wardrobeservices.dto.request.ForgotPasswordRequest;
import com.example.wardrobeservices.dto.request.LoginRequest;
import com.example.wardrobeservices.dto.request.RefreshTokenRequest;
import com.example.wardrobeservices.dto.request.ResetPasswordRequest;
import com.example.wardrobeservices.dto.response.AuthResponse;


public interface AuthService {
    
    AuthResponse login(LoginRequest request);
    
    AuthResponse refreshToken(RefreshTokenRequest request);
    
    void logout(RefreshTokenRequest request, String accessToken);

    // === Đổi & Quên mật khẩu ===
    void changePassword(ChangePasswordRequest request);       // User đã đăng nhập, đổi mật khẩu cũ → mới
    void forgotPassword(ForgotPasswordRequest request);       // Gửi OTP 6 số đến email
    void resetPassword(ResetPasswordRequest request);         // Xác thực OTP + đặt mật khẩu mới
}

