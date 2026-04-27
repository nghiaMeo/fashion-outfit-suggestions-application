package com.example.wardrobeservices.service;

import com.example.wardrobeservices.dto.request.UserCreationRequest;
import com.example.wardrobeservices.dto.response.UserResponse;

public interface UserService {
    UserResponse register(UserCreationRequest request);
}
