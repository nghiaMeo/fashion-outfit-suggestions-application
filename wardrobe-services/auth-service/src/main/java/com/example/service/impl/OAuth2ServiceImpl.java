package com.example.service.impl;

import com.example.dto.request.OAuth2Request;
import com.example.dto.response.AuthResponse;
import com.example.dto.response.UserResponse;
import com.example.entity.User;
import com.example.entity.UserPreference;
import com.example.entity.enums.AuthProvider;
import com.example.entity.enums.Role;
import com.example.exception.AppException;
import com.example.exception.ErrorCode;
import com.example.repository.UserPreferenceRepository;
import com.example.repository.UserRepository;
import com.example.service.JwtService;
import com.example.service.OAuth2Service;
import com.example.service.RefreshTokenService;
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
        // 1. Kiểm tra Token của Google
        // Khi Frontend cho user đăng nhập Google, Google trả về 1 chuỗi Token. Frontend đưa Token đó cho Backend.
        // Backend phải mang Token này qua tận nhà Google để hỏi: "Token này có thật không?"
        Map<String, Object> googleUser = verifyGoogleToken(request.getToken());

        // 2. Trích xuất thông tin
        // Nếu Google bảo thật, Google sẽ trả về thông tin của User. Ta lấy ra dùng.
        var email = (String) googleUser.get("email");
        var googleId = (String) googleUser.get("sub"); // 'sub' là ID duy nhất của user trên Google
        var name = (String) googleUser.get("name");
        var picture = (String) googleUser.get("picture");

        // 3. TÌM HOẶC TẠO MỚI TÀI KHOẢN (UPSERT LOGIC)
        // Đầu tiên: Tìm xem trong DB đã có ai liên kết với cái ID Google này chưa?
        User user = userRepository.findByProviderAndProviderId(AuthProvider.GOOGLE, googleId)
                .orElseGet(() -> 
                        // Nếu chưa có, tìm tiếp xem có ai xài chung cái Email này (tạo bằng tay) không?
                        userRepository.findByEmail(email)
                        // Nếu vẫn không có ai, nghĩa là ông này mới đến lần đầu -> Tự động đăng ký cho ổng luôn!
                        .orElseGet(() -> createOAuth2User(email, name, picture, AuthProvider.GOOGLE, googleId))
                );

        // 4. Cấp thẻ bài (Token) của hệ thống mình cho họ xài
        return buildAuthResponse(user);
    }

    // Hàm gọi API sang server Google để xác minh Token
    @SuppressWarnings("unchecked")
    private Map<String, Object> verifyGoogleToken(String idToken) {
        try {
            // Dùng RestClient gửi request GET tới Endpoint của Google
            return restClient.get()
                    .uri("https://oauth2.googleapis.com/tokeninfo?id_token={token}", idToken)
                    .retrieve()
                    .body(Map.class); // Ép kết quả JSON trả về thành kiểu Map (Key-Value) của Java
        } catch (Exception e) {
            log.error("Google token verification failed: {}", e.getMessage());
            throw new AppException(ErrorCode.OAUTH2_INVALID_TOKEN);
        }
    }

    // Hàm tạo tài khoản ẩn danh cho người dùng đăng nhập lần đầu bằng Google
    private User createOAuth2User(String email, String name, String picture, AuthProvider provider, String providerId) {
        var user = User.builder()
                .email(email)
                .username(generateUniqueUsername(name)) // Username không được trùng, nên phải chế ra một cái ngẫu nhiên
                .displayName(name)
                .avatarUrl(picture)
                .provider(provider) // Lưu vết: Ông này vào nhà bằng cổng GOOGLE
                .providerId(providerId)
                .role(Role.USER)
                .build();

        user = userRepository.save(user);

        // Đồng thời khởi tạo luôn bảng Tùy chọn (Preference) trống cho họ
        UserPreference preference = UserPreference.builder()
                .user(user)
                .build();
        userPreferenceRepository.save(preference);

        return user;
    }

    // Hàm tự động sinh Username (VD: "nghia_a8b9c1")
    private String generateUniqueUsername(String name) {
        var baseUsername = name != null
                ? name.toLowerCase().replaceAll("\\s+", "") // Xóa khoảng trắng
                : "user";
        // Cộng thêm 6 ký tự ngẫu nhiên của UUID để đảm bảo không ai trùng ai
        return baseUsername + "_" + UUID.randomUUID().toString().substring(0, 6);
    }

    // Hàm tiện ích: Đóng gói Token và trả về cho Frontend
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
