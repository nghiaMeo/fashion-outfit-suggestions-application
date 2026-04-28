package com.example.wardrobeservices.service;

import com.example.wardrobeservices.dto.request.UserCreationRequest;
import com.example.wardrobeservices.dto.response.UserResponse;

public interface UserService {
    /**
 * Register a new user account.
 *
 * @param request the data required to create the user account
 * @return a UserResponse representing the newly registered user
 */
UserResponse register(UserCreationRequest request);
}
