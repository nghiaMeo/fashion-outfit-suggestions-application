package com.example.client;

import com.example.dto.request.NotificationRequest;
import com.example.dto.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "notification-service", path = "/api/notifications")
public interface NotificationClient {

    @PostMapping("/send")
    ApiResponse<Void> sendNotification(@RequestBody NotificationRequest request);
}
