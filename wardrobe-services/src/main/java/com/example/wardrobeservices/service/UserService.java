package com.example.wardrobeservices.service;

import com.example.wardrobeservices.dto.request.UserCreationRequest;
import com.example.wardrobeservices.dto.response.UserProfileResponse;
import com.example.wardrobeservices.dto.response.UserResponse;

import java.util.UUID;

public interface UserService {
    
    UserResponse register(UserCreationRequest request);

    UserProfileResponse getUserProfile(UUID userId);

}

