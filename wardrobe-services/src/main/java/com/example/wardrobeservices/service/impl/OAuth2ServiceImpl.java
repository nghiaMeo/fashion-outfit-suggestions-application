package com.example.wardrobeservices.service.impl;

import com.example.wardrobeservices.dto.request.OAuth2Request;
import com.example.wardrobeservices.dto.response.AuthResponse;
import com.example.wardrobeservices.dto.response.UserResponse;
import com.example.wardrobeservices.entity.RefreshToken;
import com.example.wardrobeservices.entity.User;
import com.example.wardrobeservices.entity.UserPreference;
import com.example.wardrobeservices.entity.enums.AuthProvider;
import com.example.wardrobeservices.entity.enums.Role;
import com.example.wardrobeservices.exception.AppException;
import com.example.wardrobeservices.exception.ErrorCode;
import com.example.wardrobeservices.repository.UserPreferenceRepository;
import com.example.wardrobeservices.repository.UserRepository;
import com.example.wardrobeservices.service.JwtService;
import com.example.wardrobeservices.service.OAuth2Service;
import com.example.wardrobeservices.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${oauth2.google.client-id}")
    private String googleClientId;

    private final RestClient restClient = RestClient.create();

    @Override
    @Transactional
    public AuthResponse loginWithGoogle(OAuth2Request request) {
        // Verify Google ID Token
        Map<String, Object> googleUser = verifyGoogleToken(request.getToken());

        String email = (String) googleUser.get("email");
        String googleId = (String) googleUser.get("sub");
        String name = (String) googleUser.get("name");
        String picture = (String) googleUser.get("picture");

        // Find existing user or create new one
        User user = userRepository.findByProviderAndProviderId(AuthProvider.GOOGLE, googleId)
                .orElseGet(() -> userRepository.findByEmail(email)
                        .orElseGet(() -> createOAuth2User(email, name, picture, AuthProvider.GOOGLE, googleId)));

        return buildAuthResponse(user);
    }

    @Override
    @Transactional
    public AuthResponse loginWithFacebook(OAuth2Request request) {
        // Verify Facebook Access Token
        Map<String, Object> fbUser = verifyFacebookToken(request.getToken());

        String email = (String) fbUser.get("email");
        String facebookId = (String) fbUser.get("id");
        String name = (String) fbUser.get("name");

        // Extract profile picture URL
        final String picture = extractFacebookPicture(fbUser);

        User user = userRepository.findByProviderAndProviderId(AuthProvider.FACEBOOK, facebookId)
                .orElseGet(() -> userRepository.findByEmail(email)
                        .orElseGet(() -> createOAuth2User(email, name, picture, AuthProvider.FACEBOOK, facebookId)));

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

    @SuppressWarnings("unchecked")
    private Map<String, Object> verifyFacebookToken(String accessToken) {
        try {
            return restClient.get()
                    .uri("https://graph.facebook.com/me?fields=id,name,email,picture&access_token={token}", accessToken)
                    .retrieve()
                    .body(Map.class);
        } catch (Exception e) {
            log.error("Facebook token verification failed: {}", e.getMessage());
            throw new AppException(ErrorCode.OAUTH2_INVALID_TOKEN);
        }
    }

    private User createOAuth2User(String email, String name, String picture, AuthProvider provider, String providerId) {
        User user = User.builder()
                .email(email)
                .username(generateUniqueUsername(name))
                .avatarUrl(picture)
                .provider(provider)
                .providerId(providerId)
                .role(Role.USER)
                .build();

        user = userRepository.save(user);

        // Create default preference
        UserPreference preference = UserPreference.builder()
                .user(user)
                .build();
        userPreferenceRepository.save(preference);

        return user;
    }

    private String generateUniqueUsername(String name) {
        String baseUsername = name != null
                ? name.toLowerCase().replaceAll("\\s+", "")
                : "user";
        String username = baseUsername + "_" + UUID.randomUUID().toString().substring(0, 6);
        return username;
    }

    private String extractFacebookPicture(Map<String, Object> fbUser) {
        Object pictureObj = fbUser.get("picture");
        if (pictureObj instanceof Map<?, ?> pictureMap) {
            Object dataObj = pictureMap.get("data");
            if (dataObj instanceof Map<?, ?> dataMap) {
                return (String) dataMap.get("url");
            }
        }
        return null;
    }

    private AuthResponse buildAuthResponse(User user) {
        String accessToken = jwtService.generateAccessToken(user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

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
