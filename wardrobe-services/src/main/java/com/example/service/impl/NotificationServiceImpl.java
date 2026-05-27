package com.example.service.impl;

import com.corundumstudio.socketio.SocketIOServer;
import com.example.dto.NotificationResponse;
import com.example.entity.Notification;
import com.example.entity.User;
import com.example.exception.AppException;
import com.example.exception.ErrorCode;
import com.example.repository.NotificationRepository;
import com.example.entity.enums.NotificationType;
import com.example.service.NotificationService;
import com.example.service.UserService;
import com.google.firebase.messaging.FirebaseMessaging;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final SocketIOServer socketIOServer;
    private final UserService userService;

    public NotificationServiceImpl(
            NotificationRepository notificationRepository,
            @Qualifier("notificationSocketIOServer") SocketIOServer socketIOServer,
            UserService userService) {
        this.notificationRepository = notificationRepository;
        this.socketIOServer = socketIOServer;
        this.userService = userService;
    }

    private User getCurrentUser() {
        return (User) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();
    }

    @Override
    public void sendPushNotification(String token, String title, String body) {
        com.google.firebase.messaging.Message fcmMessage = com.google.firebase.messaging.Message.builder()
                .setToken(token)
                .setNotification(com.google.firebase.messaging.Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .build();

        try {
            FirebaseMessaging.getInstance().send(fcmMessage);
            System.out.println("token");
        } catch (Exception e) {
            System.err.println(" + e.getMessage()");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getMyNotifications() {
        var currentUser = getCurrentUser();
        var notifications = notificationRepository.findByRecipientIdOrderByCreatedAtDesc(currentUser.getId());
        
        return notifications.stream().map(this::mapToResponse).toList();
    }

    @Override
    public void markAsRead(UUID notificationId) {
        var currentUser = getCurrentUser();
        var notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new AppException(ErrorCode.NOTIFICATION_NOT_FOUND));

        if (!notification.getRecipientId().equals(currentUser.getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        notification.setRead(true);
        notificationRepository.save(notification);
    }

    @Override
    public void markAllAsRead() {
        var currentUser = getCurrentUser();
        var notifications = notificationRepository.findByRecipientIdOrderByCreatedAtDesc(currentUser.getId());
        
        for (Notification notification : notifications) {
            if (!notification.isRead()) {
                notification.setRead(true);
            }
        }
        
        notificationRepository.saveAll(notifications);
    }

    @Override
    @Transactional(readOnly = true)
    public long getUnreadCount() {
        var currentUser = getCurrentUser();
        return notificationRepository.countByRecipientIdAndIsReadFalse(currentUser.getId());
    }

    @Override
    public void sendNotification(UUID recipientId, UUID actorId, NotificationType type, UUID targetId, String content) {
        Notification notification = Notification.builder()
                .recipientId(recipientId)
                .actorId(actorId)
                .type(type)
                .targetId(targetId)
                .content(content)
                .build();

        var savedNotification  = notificationRepository.save(notification);

        socketIOServer.getRoomOperations(recipientId.toString())
                .sendEvent("new_notification", mapToResponse(savedNotification));

        try {
            String token = userService.getFcmToken(recipientId);
            if (token != null && !token.trim().isEmpty()) {
                sendPushNotification(token, "Bạn có thông báo mới", content);
            }
        } catch (Exception e) {
            // Non-blocking fallback
        }
    }

    private NotificationResponse mapToResponse(Notification notification) {
        String actorName = "User";
        String actorAvatar = null;
        try {
            var profile = userService.getUserProfile(notification.getActorId());
            if (profile != null) {
                actorName = profile.getDisplayName();
                actorAvatar = profile.getAvatarUrl();
            }
        } catch (Exception e) {
            // Fallback
        }

        return NotificationResponse.builder()
                .id(notification.getId())
                .actorId(notification.getActorId())
                .actorName(actorName)
                .actorAvatar(actorAvatar)
                .type(notification.getType())
                .targetId(notification.getTargetId())
                .content(notification.getContent())
                .isRead(notification.isRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
