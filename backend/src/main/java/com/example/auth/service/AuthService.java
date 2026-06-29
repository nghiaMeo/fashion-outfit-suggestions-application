package com.example.auth.service;

import com.example.auth.dto.AuthResponse;
import com.example.auth.dto.LoginRequest;
import com.example.auth.dto.RegisterRequest;
import com.example.auth.entity.User;
import com.example.auth.repository.UserRepository;
import com.example.common.exception.AppException;
import com.example.common.exception.AuthenticationException;
import com.example.common.exception.ErrorCode;
import com.example.common.security.JwtTokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Authentication Service
 * Handles user registration, login, and token management
 */
@Slf4j
@Service
@Transactional
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    /**
     * Register a new user
     */
    public AuthResponse register(RegisterRequest request) {
        log.info("Registering new user with email: {}", request.getEmail());

        // Check if user already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.USER_ALREADY_EXISTS, "Email already exists");
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AppException(ErrorCode.USER_ALREADY_EXISTS, "Username already exists");
        }

        // Create new user
        User user = User.builder()
                .email(request.getEmail())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .status(User.UserStatus.ACTIVE)
                .role(User.UserRole.USER)
                .emailVerified(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        user = userRepository.save(user);
        log.info("User registered successfully with id: {}", user.getId());

        return buildAuthResponse(user);
    }

    /**
     * Login user
     */
    public AuthResponse login(LoginRequest request) {
        log.info("Login attempt for email: {}", request.getEmail());

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AuthenticationException(
                    ErrorCode.INVALID_CREDENTIALS, 
                    "Email or password is incorrect"
                ));

        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AuthenticationException(
                ErrorCode.INVALID_CREDENTIALS, 
                "Email or password is incorrect"
            );
        }

        // Check user status
        if (user.getStatus() == User.UserStatus.INACTIVE) {
            throw new AuthenticationException(
                ErrorCode.USER_INACTIVE, 
                "User account is inactive"
            );
        }

        // Update last login time
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        log.info("User logged in successfully: {}", user.getId());
        return buildAuthResponse(user);
    }

    /**
     * Refresh access token using refresh token
     */
    public AuthResponse refreshToken(String refreshToken) {
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new AuthenticationException(ErrorCode.TOKEN_INVALID, "Invalid refresh token");
        }

        String userId = jwtTokenProvider.getUserIdFromToken(refreshToken);
        User user = userRepository.findById(Long.parseLong(userId))
                .orElseThrow(() -> new AuthenticationException(
                    ErrorCode.USER_NOT_FOUND, 
                    "User not found"
                ));

        return buildAuthResponse(user);
    }

    /**
     * Build authentication response
     */
    private AuthResponse buildAuthResponse(User user) {
        String accessToken = jwtTokenProvider.generateAccessToken(String.valueOf(user.getId()));
        String refreshToken = jwtTokenProvider.generateRefreshToken(String.valueOf(user.getId()));

        AuthResponse.UserInfoDto userInfo = AuthResponse.UserInfoDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .profilePictureUrl(user.getProfilePictureUrl())
                .build();

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .user(userInfo)
                .build();
    }
}
