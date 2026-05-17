package com.example.controller;

import com.example.dto.request.ProfileUpdateRequest;
import com.example.dto.response.ApiResponse;
import com.example.dto.response.UserProfileResponse;
import com.example.entity.User;
import com.example.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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

    @GetMapping("/search")
    public ApiResponse<List<UserProfileResponse>> searchUsers(@RequestParam String query,
                                                              @RequestParam UUID currentUserId) {
        return ApiResponse.<java.util.List<UserProfileResponse>>builder()
                .result(userService.searchUsers(query, currentUserId))
                .build();
    }

    @PostMapping("/profile/batch")
    public ApiResponse<List<UserProfileResponse>> getUsersProfiles(@RequestBody List<UUID> userIds) {
        return ApiResponse.<List<UserProfileResponse>>builder()
                .result(userService.getUsersProfiles(userIds))
                .build();
    }
}
