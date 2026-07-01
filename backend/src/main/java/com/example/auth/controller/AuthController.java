package com.example.auth.controller;

import com.example.auth.dto.request.*;
import com.example.auth.dto.response.AuthResponse;
import com.example.auth.service.AuthService;
import com.example.auth.service.OAuth2Service;
import com.example.common.dto.ApiResponse;
import com.example.user.dto.request.UserCreationRequest;
import com.example.user.dto.response.UserResponse;
import com.example.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthService authService;
    private final OAuth2Service oAuth2Service;

    @PostMapping("/register")
    public ApiResponse<UserResponse> register(@RequestBody @Valid UserCreationRequest request) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.register(request))
                .build();
    }

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@RequestBody @Valid LoginRequest request) {
        return ApiResponse.<AuthResponse>builder()
                .result(authService.login(request))
                .build();
    }

    @PostMapping("/refresh-token")
    public ApiResponse<AuthResponse> refreshToken(@RequestBody @Valid RefreshTokenRequest request) {
        return ApiResponse.<AuthResponse>builder()
                .result(authService.refreshToken(request))
                .build();
    }

    @PostMapping("/oauth2/google")
    public ApiResponse<AuthResponse> loginWithGoogle(@RequestBody @Valid OAuth2Request request) {
        return ApiResponse.<AuthResponse>builder()
                .result(oAuth2Service.loginWithGoogle(request))
                .build();
    }

    @PostMapping("/logout")
    public ApiResponse<String> logout(
            @RequestBody @Valid RefreshTokenRequest request,
            @RequestHeader("Authorization") String authHeader
    ) {
        authService.logout(request, authHeader);
        return ApiResponse.<String>builder()
                .result("Logout successfully")
                .build();
    }

    @PutMapping("/change-password")
    public ApiResponse<String> changePassword(@RequestBody @Valid ChangePasswordRequest request) {
        authService.changePassword(request);
        return ApiResponse.<String>builder()
                .result("Password changed successfully")
                .build();
    }

    @PostMapping("/forgot-password")
    public ApiResponse<String> forgotPassword(@RequestBody @Valid ForgotPasswordRequest request) {
        authService.forgotPassword(request);
        return ApiResponse.<String>builder()
                .result("OTP has been sent to your email")
                .build();
    }

    @PostMapping("/reset-password")
    public ApiResponse<String> resetPassword(@RequestBody @Valid ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ApiResponse.<String>builder()
                .result("Password has been reset successfully")
                .build();
    }
}
