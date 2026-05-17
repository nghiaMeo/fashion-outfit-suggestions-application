package com.example.wardrobeservices.service;

import com.example.wardrobeservices.dto.response.NotificationResponse;
import com.example.wardrobeservices.entity.User;
import com.example.wardrobeservices.entity.enums.NotificationType;

import java.util.List;
import java.util.UUID;

public interface NotificationService {

    List<NotificationResponse> getMyNotifications();

    void markAsRead(UUID notificationId);

    void markAllAsRead();

    long getUnreadCount();

    void sendPushNotification(String token, String title, String body);

    void sendNotification(User recipient, User actor, NotificationType type, UUID targetId, String content);
}
