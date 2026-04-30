package com.example.wardrobeservices.controller;

import com.example.wardrobeservices.dto.request.LoginRequest;
import com.example.wardrobeservices.dto.request.OAuth2Request;
import com.example.wardrobeservices.dto.request.RefreshTokenRequest;
import com.example.wardrobeservices.dto.request.UserCreationRequest;
import com.example.wardrobeservices.dto.response.ApiResponse;
import com.example.wardrobeservices.dto.response.AuthResponse;
import com.example.wardrobeservices.dto.response.UserResponse;
import com.example.wardrobeservices.service.AuthService;
import com.example.wardrobeservices.service.OAuth2Service;
import com.example.wardrobeservices.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthService authService;
    private final OAuth2Service oAuth2Service;

    /**
     * Create a new user from the provided registration payload and return the created user's data.
     *
     * @param request the registration request payload; validated via Bean Validation annotations
     * @return an ApiResponse whose result is the created user's UserResponse
     */
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
}
