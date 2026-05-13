package com.example.wardrobeservices.controller;

import com.example.wardrobeservices.dto.request.ProfileUpdateRequest;
import com.example.wardrobeservices.dto.response.ApiResponse;
import com.example.wardrobeservices.dto.response.UserProfileResponse;
import com.example.wardrobeservices.dto.response.UserResponse;
import com.example.wardrobeservices.entity.User;
import com.example.wardrobeservices.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/my-profile")
    public ApiResponse<UserProfileResponse> getMyProfile() {
        var currentUser = (User) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();

        return ApiResponse.<UserProfileResponse>builder()
                .result(userService.getUserProfile(currentUser.getId()))
                .build();
    }

    @GetMapping("/profile/{userId}")
    public ApiResponse<UserProfileResponse> getProfile(@PathVariable UUID userId) {
        return ApiResponse.<UserProfileResponse>builder()
                .result(userService.getUserProfile(userId))
                .build();
    }

    @PutMapping("/profile")
    public ApiResponse<UserProfileResponse> updateProfile(@RequestBody @Valid ProfileUpdateRequest request) {
        return ApiResponse.<UserProfileResponse>builder()
                .result(userService.updateProfile(request))
                .build();
    }
}
