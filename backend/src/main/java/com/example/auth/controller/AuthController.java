package com.example.auth.controller;

import com.example.auth.dto.request.LoginRequest;
import com.example.auth.dto.request.OAuth2Request;
import com.example.auth.dto.request.RefreshTokenRequest;
import com.example.auth.dto.response.AuthResponse;
import com.example.auth.service.AuthService;
import com.example.auth.service.OAuth2Service;
import com.example.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;


@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "User authentication and authorization endpoints")
public class AuthController {

    private final AuthService authService;
    private final OAuth2Service oAuth2Service;

    @PostMapping("/register")
    @Operation(
            summary = "Register new user",
            description = "Create a new user account with email, username, and password"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "User registered successfully",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Email or username already exists"
            )
    })
    public ResponseEntity<?> register(@Valid @RequestBody LoginRequest request) {
        log.info("Register request for email: {}", request.getEmail());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.builder().result(authService.register(request)).build());
    }

    @PostMapping("/login")
    @Operation(
            summary = "Login with email and password",
            description = "Authenticate user and receive JWT tokens"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Login successful"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Invalid credentials"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "423",
                    description = "Account locked due to too many failed attempts"
            )
    })
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        log.info("Login request for email: {}", request.getEmail());
        return ResponseEntity.ok(
                ApiResponse.builder().result(authService.login(request)).build()
        );
    }

    @PostMapping("/oauth2/google")
    @Operation(
            summary = "Login with Google OAuth2",
            description = "Authenticate using Google ID token. Creates account if not exists."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Google login successful"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Invalid Google token"
            )
    })
    public ResponseEntity<?> loginWithGoogle(@Valid @RequestBody OAuth2Request request) {
        log.info("Google OAuth2 login attempt");
        return ResponseEntity.ok(
                ApiResponse.builder().result(oAuth2Service.loginWithGoogle(request)).build()
        );
    }

    @PostMapping("/refresh-token")
    @Operation(
            summary = "Refresh access token",
            description = "Get new access token using refresh token"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Token refreshed successfully"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Refresh token expired or invalid"
            )
    })
    public ResponseEntity<?> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        log.info("Refresh token request");
        return ResponseEntity.ok(
                ApiResponse.builder().result(authService.refreshToken(request)).build()
        );
    }

    @PostMapping("/logout")
    @Operation(
            summary = "Logout user",
            description = "Logout and blacklist refresh token",
            security = @io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Logout successful"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized"
            )
    })
    public ResponseEntity<?> logout(@Valid @RequestBody RefreshTokenRequest request,
                                    @RequestHeader("Authorization") String token) {
        log.info("Logout request");
        authService.logout(request, token);
        return ResponseEntity.ok(
                ApiResponse.builder().result("Logged out successfully").build()
        );
    }

    @PostMapping("/forgot-password")
    @Operation(
            summary = "Request password reset",
            description = "Send OTP to email for password reset"
    )
    public ResponseEntity<?> forgotPassword(@RequestParam String email) {
        log.info("Forgot password request for email: {}", email);
        authService.forgotPassword(email);
        return ResponseEntity.ok(
                ApiResponse.builder()
                        .result("OTP sent to email")
                        .build()
        );
    }

    @PostMapping("/reset-password")
    @Operation(
            summary = "Reset password with OTP",
            description = "Reset password using email and OTP code"
    )
    public ResponseEntity<?> resetPassword(@RequestParam String email,
                                          @RequestParam String otp,
                                          @RequestParam String newPassword) {
        log.info("Reset password request for email: {}", email);
        authService.resetPassword(email, otp, newPassword);
        return ResponseEntity.ok(
                ApiResponse.builder()
                        .result("Password reset successfully")
                        .build()
        );
    }
}
