package com.example.auth.controller;

import com.example.auth.dto.request.*;
import com.example.auth.dto.response.AuthResponse;
import com.example.auth.service.AuthService;
import com.example.auth.service.OAuth2Service;
import com.example.common.dto.ApiResponse;
import com.example.user.dto.request.UserCreationRequest;
import com.example.user.dto.response.UserResponse;
import com.example.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "User authentication and authorization endpoints")
public class AuthController {

    private final UserService userService;
    private final AuthService authService;
    private final OAuth2Service oAuth2Service;

    @PostMapping("/register")
    @Operation(summary = "Register new user", description = "Create a new user account with email, username, and password")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "User registered successfully",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Email or username already exists")
    })
    public ResponseEntity<ApiResponse<UserResponse>> register(@Valid @RequestBody UserCreationRequest request) {
        log.info("Register request for email: {}", request.getEmail());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.<UserResponse>builder().result(userService.register(request)).build());
    }

    @PostMapping("/login")
    @Operation(summary = "Login with email and password", description = "Authenticate user and receive JWT tokens")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        log.info("Login request for email: {}", request.getEmail());
        return ResponseEntity.ok(ApiResponse.<AuthResponse>builder().result(authService.login(request)).build());
    }

    @PostMapping("/oauth2/google")
    @Operation(summary = "Login with Google OAuth2", description = "Authenticate using Google ID token. Creates account if not exists.")
    public ResponseEntity<ApiResponse<AuthResponse>> loginWithGoogle(@Valid @RequestBody OAuth2Request request) {
        log.info("Google OAuth2 login attempt");
        return ResponseEntity.ok(ApiResponse.<AuthResponse>builder().result(oAuth2Service.loginWithGoogle(request)).build());
    }

    @PostMapping("/refresh-token")
    @Operation(summary = "Refresh access token", description = "Get new access token using refresh token")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        log.info("Refresh token request");
        return ResponseEntity.ok(ApiResponse.<AuthResponse>builder().result(authService.refreshToken(request)).build());
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout user", description = "Logout and blacklist refresh token")
    public ResponseEntity<ApiResponse<String>> logout(
            @Valid @RequestBody RefreshTokenRequest request,
            @RequestHeader("Authorization") String token
    ) {
        log.info("Logout request");
        authService.logout(request, token);
        return ResponseEntity.ok(ApiResponse.<String>builder().result("Logged out successfully").build());
    }

    @PutMapping("/change-password")
    @Operation(summary = "Change password", description = "Change password for authenticated user")
    public ResponseEntity<ApiResponse<String>> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        authService.changePassword(request);
        return ResponseEntity.ok(ApiResponse.<String>builder().result("Password changed successfully").build());
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Request password reset", description = "Send OTP to email for password reset")
    public ResponseEntity<ApiResponse<String>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        log.info("Forgot password request for email: {}", request.getEmail());
        authService.forgotPassword(request);
        return ResponseEntity.ok(ApiResponse.<String>builder().result("OTP has been sent to your email").build());
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Reset password with OTP", description = "Reset password using email and OTP code")
    public ResponseEntity<ApiResponse<String>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        log.info("Reset password request for email: {}", request.getEmail());
        authService.resetPassword(request);
        return ResponseEntity.ok(ApiResponse.<String>builder().result("Password has been reset successfully").build());
    }
}
