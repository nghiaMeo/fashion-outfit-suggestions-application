package com.example.service;

import com.example.dto.ProfileUpdateRequest;
import com.example.dto.UserCreationRequest;
import com.example.dto.UserProfileResponse;
import com.example.dto.UserResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface UserService {
    
    UserResponse register(UserCreationRequest request);

    UserProfileResponse getUserProfile(UUID userId);

    UserProfileResponse updateProfile(ProfileUpdateRequest request);

    java.util.List<UserProfileResponse> searchUsers(String query, UUID currentUserId);

    List<UserProfileResponse> getUsersProfiles(java.util.List<UUID> userIds);

    java.util.List<UserProfileResponse> getSuggestCandidates(UUID currentUserId);

    void updatePresence(UUID userId, boolean isOnline);

    String getFcmToken(UUID userId);
    String uploadAvatar(MultipartFile file);

}
