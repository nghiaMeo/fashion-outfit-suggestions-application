package com.example.service;

import com.example.dto.request.ProfileUpdateRequest;
import com.example.dto.request.UserCreationRequest;
import com.example.dto.response.UserProfileResponse;
import com.example.dto.response.UserResponse;

import java.util.UUID;

public interface UserService {
    
    UserResponse register(UserCreationRequest request);

    UserProfileResponse getUserProfile(UUID userId);

    UserProfileResponse updateProfile(ProfileUpdateRequest request);

    java.util.List<UserProfileResponse> searchUsers(String query, UUID currentUserId);

    java.util.List<UserProfileResponse> getUsersProfiles(java.util.List<UUID> userIds);
}

