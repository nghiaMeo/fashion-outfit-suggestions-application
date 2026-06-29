package com.example.controller;

import com.example.dto.request.NotificationRequest;
import com.example.dto.response.ApiResponse;
import com.example.dto.response.NotificationResponse;
import com.example.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ApiResponse<List<NotificationResponse>> getMyNotifications() {
        return ApiResponse.<List<NotificationResponse>>builder()
                .result(notificationService.getMyNotifications())
                .build();
    }

    @GetMapping("/unread-count")
    public ApiResponse<Long> getUnreadCount() {
        return ApiResponse.<Long>builder()
                .result(notificationService.getUnreadCount())
                .build();
    }

    @PutMapping("/{notificationId}/read")
    public ApiResponse<String> markAsRead(@PathVariable UUID notificationId) {
        notificationService.markAsRead(notificationId);
        return ApiResponse.<String>builder()
                .result("Đã đánh dấu thông báo là đã đọc")
                .build();
    }

    @PutMapping("/read-all")
    public ApiResponse<String> markAllAsRead() {
        notificationService.markAllAsRead();
        return ApiResponse.<String>builder()
                .result("Đã đánh dấu tất cả thông báo là đã đọc")
                .build();
    }

    @PostMapping("/send")
    public ApiResponse<Void> sendNotification(@RequestBody NotificationRequest request) {
        notificationService.sendNotification(
                request.getRecipientId(),
                request.getActorId(),
                request.getType(),
                request.getTargetId(),
                request.getContent()
        );
        return ApiResponse.<Void>builder().build();
    }
}
