package com.example.wardrobeservices.service.impl;

import com.example.wardrobeservices.dto.request.*;
import com.example.wardrobeservices.dto.response.AuthResponse;
import com.example.wardrobeservices.dto.response.UserResponse;
import com.example.wardrobeservices.model.PasswordResetOtp;
import com.example.wardrobeservices.model.RefreshToken;
import com.example.wardrobeservices.entity.User;
import com.example.wardrobeservices.exception.AppException;
import com.example.wardrobeservices.exception.ErrorCode;
import com.example.wardrobeservices.repository.PasswordResetOtpRepository;
import com.example.wardrobeservices.repository.UserRepository;
import com.example.wardrobeservices.service.AuthService;
import com.example.wardrobeservices.service.EmailService;
import com.example.wardrobeservices.service.JwtService;
import com.example.wardrobeservices.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
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


    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private static final long OTP_EXPIRY_SECONDS = 180;

    private static final long OTP_RATE_LIMIT_SECONDS = 60;

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_CREDENTIALS));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.INVALID_CREDENTIALS);
        }

        // gen Access Token (JWT)
        var accessToken = jwtService.generateAccessToken(user);
        
        // gen Refresh Token and save in Redis
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
                
        // allocation new Access Token
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
        // define current user from Access Token
        var currentUser = (User) Objects.requireNonNull(SecurityContextHolder
                .getContext().getAuthentication()).getPrincipal();

        // check token owner user
        RefreshToken refreshToken = refreshTokenService.findByToken(request.getRefreshToken());
        if (!refreshToken.getUserId().equals(currentUser.getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        //delete refresh token from Redis
        refreshTokenService.deleteByToken(request.getRefreshToken());

        // 4. Blacklist currently access token
        if (accessToken != null && accessToken.startsWith("Bearer ")) {
            String token = accessToken.substring(7);
            jwtService.blacklistToken(token);
        }
    }


    @Override
    @Transactional
    public void changePassword(ChangePasswordRequest request) {
        // get current user from SecurityContextHolder
        // SecurityContextHolder have information user  JwtAuthenticationFilter authenticated
        // getPrincipal() return object user when filter authenticated token
        var currentUser = (User) Objects.requireNonNull(SecurityContextHolder
                .getContext().getAuthentication()).getPrincipal();


        if (!passwordEncoder.matches(request.getOldPassword(), currentUser.getPassword())) {
            throw new AppException(ErrorCode.WRONG_PASSWORD);
        }

        currentUser.setPassword(passwordEncoder.encode(request.getNewPassword()));
        currentUser.setUpdatedAt(Instant.now());
        userRepository.save(currentUser);
    }

    // Flow: User not login yet >> enter email >> server gen OTP 6 numbers >> save Redis (3min) >> send email
    @Override
    public void forgotPassword(ForgotPasswordRequest request) {
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        // Rate limiting - check this email had send OTP recently.
        otpRepository.findByEmail(request.getEmail()).ifPresent(existingOtp -> {
            // calculator with time send OTP
            long secondsSinceLastOtp = ChronoUnit.SECONDS.between(existingOtp.getCreatedAt(), Instant.now());

            // if not ==60s user must wait
            if (secondsSinceLastOtp < OTP_RATE_LIMIT_SECONDS) {
                throw new AppException(ErrorCode.OTP_RATE_LIMITED);
            }

            // if 60s >> delete old OTP and create new OTP
            otpRepository.delete(existingOtp);
        });

        // generator OTP 6 random number (between 100000 and 999999)
        String otp = String.valueOf(100000 + SECURE_RANDOM.nextInt(900000));

        // save OTP to Redis with TTL = 3 min
        var otpEntity = PasswordResetOtp.builder()
                .email(request.getEmail())
                .otp(otp)
                .expirySeconds(OTP_EXPIRY_SECONDS)
                .build();
        otpRepository.save(otpEntity);

        // Send OTP to email user
        emailService.sendOtpEmail(request.getEmail(), otp);
    }

    // Flow: User enter email + OTP + new password -> server verify OTP -> change password
    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        //find OTP in Redis with email
        // if not -> 2 ability:
        // OTP expiration (Redis delete after 3 min)
        //   b) User never require send OTP
        var otpEntity = otpRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.OTP_EXPIRED));

        //compare OTP user enter with OTP in Redis
        if (!otpEntity.getOtp().equals(request.getOtp())) {
            throw new AppException(ErrorCode.OTP_INVALID);
        }

        //OTP true >> find user follow email
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        //hash new password and save db
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setUpdatedAt(Instant.now());
        userRepository.save(user);

        //delete OTP from Redis after used
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
}

