package com.example.wardrobeservices.controller;

import com.example.wardrobeservices.dto.response.ApiResponse;
import com.example.wardrobeservices.dto.response.UserProfileResponse;
import com.example.wardrobeservices.entity.User;
import com.example.wardrobeservices.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
