package com.example.user.service;

import com.example.user.dto.request.ProfileUpdateRequest;
import com.example.user.dto.request.UserCreationRequest;
import com.example.user.dto.response.UserProfileResponse;
import com.example.user.dto.response.UserResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface UserService {

    UserResponse register(UserCreationRequest request);

    UserProfileResponse getUserProfile(UUID userId);

    UserProfileResponse updateProfile(ProfileUpdateRequest request);

    List<UserProfileResponse> searchUsers(String query, UUID currentUserId);

    List<UserProfileResponse> getUsersProfiles(List<UUID> userIds);

    List<UserProfileResponse> getSuggestCandidates(UUID currentUserId);

    void updatePresence(UUID userId, boolean isOnline);

    String getFcmToken(UUID userId);

    String uploadAvatar(MultipartFile file);
}
