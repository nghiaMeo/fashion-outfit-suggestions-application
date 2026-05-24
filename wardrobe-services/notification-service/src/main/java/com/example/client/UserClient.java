package com.example.client;

import com.example.dto.response.ApiResponse;
import com.example.dto.response.UserProfileResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "auth-service", path = "/api/user")
public interface UserClient {

    @GetMapping("/profile/{userId}")
    ApiResponse<UserProfileResponse> getProfile(@PathVariable("userId") UUID userId);

    @GetMapping("/fcm-token/{userId}")
    ApiResponse<String> getFcmToken(@PathVariable("userId") UUID userId);
}
