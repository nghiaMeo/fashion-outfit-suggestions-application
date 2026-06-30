package com.example.auth.service.impl;

import com.example.auth.dto.request.OAuth2Request;
import com.example.auth.dto.response.AuthResponse;
import com.example.auth.service.JwtService;
import com.example.auth.service.OAuth2Service;
import com.example.auth.service.RefreshTokenService;
import com.example.user.dto.response.UserResponse;
import com.example.user.entity.User;
import com.example.user.entity.UserPreference;
import com.example.user.entity.enums.AuthProvider;
import com.example.user.entity.enums.Role;
import com.example.user.repository.UserPreferenceRepository;
import com.example.user.repository.UserRepository;
import com.example.common.exception.AppException;
import com.example.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OAuth2ServiceImpl implements OAuth2Service {

    private final UserRepository userRepository;
    private final UserPreferenceRepository userPreferenceRepository;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    private final RestClient restClient = RestClient.create();

    @Override
    @Transactional
    public AuthResponse loginWithGoogle(OAuth2Request request) {
        Map<String, Object> googleUser = verifyGoogleToken(request.getToken());

        var email = (String) googleUser.get("email");
        var googleId = (String) googleUser.get("sub");
        var name = (String) googleUser.get("name");
        var picture = (String) googleUser.get("picture");

        User user = userRepository.findByProviderAndProviderId(AuthProvider.GOOGLE, googleId)
                .orElseGet(() ->
                        userRepository.findByEmail(email)
                        .orElseGet(() -> createOAuth2User(email, name, picture, AuthProvider.GOOGLE, googleId))
                );

        return buildAuthResponse(user);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> verifyGoogleToken(String idToken) {
        try {
            return restClient.get()
                    .uri("https://oauth2.googleapis.com/tokeninfo?id_token={token}", idToken)
                    .retrieve()
                    .body(Map.class);
        } catch (Exception e) {
            log.error("Google token verification failed: {}", e.getMessage());
            throw new AppException(ErrorCode.OAUTH2_INVALID_TOKEN);
        }
    }

    private User createOAuth2User(String email, String name, String picture, AuthProvider provider, String providerId) {
        var user = User.builder()
                .email(email)
                .username(generateUniqueUsername(name))
                .displayName(name)
                .avatarUrl(picture)
                .provider(provider)
                .providerId(providerId)
                .role(Role.USER)
                .build();

        user = userRepository.save(user);

        UserPreference preference = UserPreference.builder()
                .user(user)
                .build();
        userPreferenceRepository.save(preference);

        return user;
    }

    private String generateUniqueUsername(String name) {
        var baseUsername = name != null
                ? name.toLowerCase().replaceAll("\\s+", "")
                : "user";
        return baseUsername + "_" + UUID.randomUUID().toString().substring(0, 6);
    }

    private AuthResponse buildAuthResponse(User user) {
        var accessToken = jwtService.generateAccessToken(user);
        var refreshToken = refreshTokenService.createRefreshToken(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .tokenType("Bearer")
                .user(UserResponse.builder()
                        .id(user.getId())
                        .email(user.getEmail())
                        .username(user.getUsername())
                        .avatarUrl(user.getAvatarUrl())
                        .bio(user.getBio())
                        .role(user.getRole())
                        .createdAt(user.getCreatedAt())
                        .build())
                .build();
    }
}
