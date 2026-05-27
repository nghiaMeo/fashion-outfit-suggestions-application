package com.example.service.impl;

import com.example.dto.ProfileUpdateRequest;
import com.example.dto.UserCreationRequest;
import com.example.dto.UserProfileResponse;
import com.example.dto.UserResponse;
import com.example.entity.User;
import com.example.entity.UserPreference;
import com.example.exception.AppException;
import com.example.exception.ErrorCode;
import com.example.repository.UserRepository;
import com.example.repository.UserPreferenceRepository;
import com.example.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserPreferenceRepository userPreferenceRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserResponse register(UserCreationRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AppException(ErrorCode.USERNAME_EXISTED);
        }

        var user = User.builder()
                .email(request.getEmail())
                .username(request.getUsername())
                .displayName(request.getDisplayName())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        user = userRepository.save(user);

        var preference = UserPreference.builder()
                .user(user)
                .build();
        userPreferenceRepository.save(preference);

        return mapToUserResponse(user);
    }

    @Override
    public UserProfileResponse getUserProfile(UUID userId) {
        var currentUser = (User) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();

        var targetUser = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        // DDD Note: Vì db_auth hoàn toàn độc lập với db_wardrobe và db_social,
        // Ta tạm thời đặt các chỉ số liên kết là 0/null (sẽ gọi OpenFeign ở các Phase sau)
        boolean isSelf = targetUser.getId().equals(currentUser.getId());

        if (targetUser.isPrivateProfile() && !isSelf) {
            return UserProfileResponse.builder()
                    .id(targetUser.getId())
                    .displayName(targetUser.getDisplayName())
                    .username(targetUser.getUsername())
                    .avatarUrl(targetUser.getAvatarUrl())
                    .isPrivateProfile(true)
                    .friendshipStatus(null) // Sẽ bổ sung lấy qua Feign Client sau
                    .build();
        }

        var preference = userPreferenceRepository.findByUserId(userId).orElse(null);
        var favoriteStyles = preference != null ? preference.getFavoriteStyles() : null;

        return UserProfileResponse.builder()
                .id(targetUser.getId())
                .username(targetUser.getUsername())
                .displayName(targetUser.getDisplayName())
                .avatarUrl(targetUser.getAvatarUrl())
                .bio(targetUser.getBio())
                .itemCount(0L)       // Sẽ bổ sung lấy qua Feign Client sau
                .outfitCount(0L)     // Sẽ bổ sung lấy qua Feign Client sau
                .friendCount(0L)     // Sẽ bổ sung lấy qua Feign Client sau
                .isPrivateProfile(targetUser.isPrivateProfile())
                .favoriteStyles(favoriteStyles)
                .friendshipStatus(null) // Sẽ bổ sung lấy qua Feign Client sau
                .build();
    }

    @Override
    public UserProfileResponse updateProfile(ProfileUpdateRequest request) {
        var currentUser = (User) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();

        var user = userRepository.findById(currentUser.getId()).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        user.setDisplayName(request.getDisplayName());
        user.setBio(request.getBio());
        user.setAvatarUrl(request.getAvatarUrl());
        user.setPrivateProfile(request.isPrivateProfile());
        user.setUpdatedAt(Instant.now());

        var updatedUser = userRepository.save(user);

        return getUserProfile(updatedUser.getId());
    }

    @Override
    public java.util.List<UserProfileResponse> searchUsers(String query, UUID currentUserId) {
        java.util.List<User> users = userRepository.searchUsers(query, currentUserId);
        return users.stream().map(u -> getUserProfile(u.getId())).toList();
    }

    @Override
    public java.util.List<UserProfileResponse> getUsersProfiles(java.util.List<UUID> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return java.util.Collections.emptyList();
        }
        return userIds.stream()
                .map(id -> {
                    try {
                        return getUserProfile(id);
                    } catch (Exception e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList();
    }

    @Override
    public java.util.List<UserProfileResponse> getSuggestCandidates(UUID currentUserId) {
        java.util.List<User> candidates = userRepository.findPublicUsersToSuggest(currentUserId);
        return candidates.stream()
                .map(u -> {
                    try {
                        var preference = userPreferenceRepository.findByUserId(u.getId()).orElse(null);
                        var favoriteStyles = preference != null ? preference.getFavoriteStyles() : null;
                        return UserProfileResponse.builder()
                                .id(u.getId())
                                .username(u.getUsername())
                                .displayName(u.getDisplayName())
                                .avatarUrl(u.getAvatarUrl())
                                .bio(u.getBio())
                                .isPrivateProfile(false)
                                .favoriteStyles(favoriteStyles)
                                .build();
                    } catch (Exception e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList();
    }

    @Override
    @Transactional
    public void updatePresence(UUID userId, boolean isOnline) {
        var user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            user.setOnline(isOnline);
            user.setLastSeen(java.time.Instant.now());
            userRepository.save(user);
        }
    }

    @Override
    public String getFcmToken(UUID userId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        return user.getFcmToken();
    }

    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .displayName(user.getDisplayName())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
