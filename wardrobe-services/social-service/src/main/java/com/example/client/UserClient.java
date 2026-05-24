package com.example.client;

import com.example.dto.response.ApiResponse;
import com.example.dto.response.UserProfileResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "auth-service", path = "/api/user")
public interface UserClient {

    @GetMapping("/profile/{userId}")
    ApiResponse<UserProfileResponse> getProfile(@PathVariable("userId") UUID userId);

    @GetMapping("/search")
    ApiResponse<List<UserProfileResponse>> searchUsers(@RequestParam("query") String query,
                                                        @RequestParam("currentUserId") UUID currentUserId);

    @PostMapping("/profile/batch")
    ApiResponse<List<UserProfileResponse>> getUsersProfiles(@org.springframework.web.bind.annotation.RequestBody List<UUID> userIds);

    @GetMapping("/suggest-candidates")
    ApiResponse<List<UserProfileResponse>> getSuggestCandidates(@RequestParam("currentUserId") UUID currentUserId);

    @PostMapping("/presence")
    ApiResponse<Void> updatePresence(@RequestParam("userId") java.util.UUID userId, @RequestParam("isOnline") boolean isOnline);
}
