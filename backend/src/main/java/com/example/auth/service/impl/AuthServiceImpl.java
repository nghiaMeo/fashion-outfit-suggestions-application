package com.example.auth.service.impl;

import com.example.auth.dto.request.*;
import com.example.auth.dto.response.AuthResponse;
import com.example.auth.entity.PasswordResetOtp;
import com.example.auth.entity.RefreshToken;
import com.example.auth.repository.PasswordResetOtpRepository;
import com.example.auth.service.AuthService;
import com.example.auth.service.EmailService;
import com.example.auth.service.JwtService;
import com.example.auth.service.RefreshTokenService;
import com.example.user.dto.response.UserResponse;
import com.example.user.entity.User;
import com.example.user.repository.UserRepository;
import com.example.common.exception.AppException;
import com.example.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final PasswordResetOtpRepository otpRepository;
    private final EmailService emailService;
    private final StringRedisTemplate redisTemplate;

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final long OTP_EXPIRY_SECONDS = 180;
    private static final long OTP_RATE_LIMIT_SECONDS = 60;

    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final Duration LOCK_DURATION = Duration.ofMinutes(15);

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
        String lockKey = "login:locked:" + request.getEmail();
        if (Boolean.TRUE.equals(redisTemplate.hasKey(lockKey))) {
            throw new AppException(ErrorCode.ACCOUNT_LOCKED);
        }

        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_CREDENTIALS));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            handleFailedLogin(request.getEmail());
            throw new AppException(ErrorCode.INVALID_CREDENTIALS);
        }

        clearFailedAttempts(request.getEmail());

        var accessToken = jwtService.generateAccessToken(user);
        var refreshToken = refreshTokenService.createRefreshToken(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .tokenType("Bearer")
                .user(toUserResponse(user))
                .build();
    }

    @Override
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        var refreshToken = refreshTokenService.findByToken(request.getRefreshToken());
        refreshToken = refreshTokenService.verifyExpiration(refreshToken);

        var user = userRepository.findById(refreshToken.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        String accessToken = jwtService.generateAccessToken(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .tokenType("Bearer")
                .user(toUserResponse(user))
                .build();
    }

    @Override
    @Transactional
    public void logout(RefreshTokenRequest request, String accessToken) {
        var currentUser = (User) Objects.requireNonNull(SecurityContextHolder
                .getContext().getAuthentication()).getPrincipal();

        RefreshToken refreshToken = refreshTokenService.findByToken(request.getRefreshToken());
        if (!refreshToken.getUserId().equals(currentUser.getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        refreshTokenService.deleteByToken(request.getRefreshToken());

        if (accessToken != null && accessToken.startsWith("Bearer ")) {
            String token = accessToken.substring(7);
            jwtService.blacklistToken(token);
        }
    }

    @Override
    @Transactional
    public void changePassword(ChangePasswordRequest request) {
        var currentUser = (User) Objects.requireNonNull(SecurityContextHolder
                .getContext().getAuthentication()).getPrincipal();

        if (!passwordEncoder.matches(request.getOldPassword(), currentUser.getPassword())) {
            throw new AppException(ErrorCode.WRONG_PASSWORD);
        }

        currentUser.setPassword(passwordEncoder.encode(request.getNewPassword()));
        currentUser.setUpdatedAt(Instant.now());
        userRepository.save(currentUser);
    }

    @Override
    public void forgotPassword(ForgotPasswordRequest request) {
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        otpRepository.findByEmail(request.getEmail()).ifPresent(existingOtp -> {
            long secondsSinceLastOtp = ChronoUnit.SECONDS.between(existingOtp.getCreatedAt(), Instant.now());
            if (secondsSinceLastOtp < OTP_RATE_LIMIT_SECONDS) {
                throw new AppException(ErrorCode.OTP_RATE_LIMITED);
            }
            otpRepository.delete(existingOtp);
        });

        String otp = String.valueOf(100000 + SECURE_RANDOM.nextInt(900000));

        var otpEntity = PasswordResetOtp.builder()
                .email(request.getEmail())
                .otp(otp)
                .expirySeconds(OTP_EXPIRY_SECONDS)
                .build();
        otpRepository.save(otpEntity);

        emailService.sendOtpEmail(request.getEmail(), otp);
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        var otpEntity = otpRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.OTP_EXPIRED));

        if (!otpEntity.getOtp().equals(request.getOtp())) {
            throw new AppException(ErrorCode.OTP_INVALID);
        }

        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setUpdatedAt(Instant.now());
        userRepository.save(user);

        otpRepository.delete(otpEntity);
    }

    private UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .avatarUrl(user.getAvatarUrl())
                .bio(user.getBio())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .build();
    }

    private void handleFailedLogin(String email) {
        String failedKey = "login:failed:" + email;
        Long attempts = redisTemplate.opsForValue().increment(failedKey);
        if (attempts != null) {
            if (attempts == 1) {
                redisTemplate.expire(failedKey, LOCK_DURATION);
            }
            if (attempts >= MAX_FAILED_ATTEMPTS) {
                String lockKey = "login:locked:" + email;
                redisTemplate.opsForValue().set(lockKey, "true", LOCK_DURATION);
                redisTemplate.delete(failedKey);
            }
        }
    }

    private void clearFailedAttempts(String email) {
        redisTemplate.delete("login:failed:" + email);
    }
}
