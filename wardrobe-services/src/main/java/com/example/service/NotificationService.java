package com.example.service;

import com.example.dto.response.NotificationResponse;
import com.example.entity.enums.NotificationType;

import java.util.List;
import java.util.UUID;

public interface NotificationService {

    List<NotificationResponse> getMyNotifications();

    void markAsRead(UUID notificationId);

    void markAllAsRead();

    long getUnreadCount();

    void sendPushNotification(String token, String title, String body);

    void sendNotification(UUID recipientId, UUID actorId, NotificationType type, UUID targetId, String content);
}
