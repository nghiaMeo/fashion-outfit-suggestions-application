package com.example.notification.service.impl;

import com.example.notification.dto.response.NotificationResponse;
import com.example.notification.entity.Notification;
import com.example.user.entity.User;
import com.example.common.exception.AppException;
import com.example.common.exception.ErrorCode;
import com.example.notification.repository.NotificationRepository;
import com.example.notification.entity.enums.NotificationType;
import com.example.notification.service.NotificationService;
import com.example.user.service.UserService;
import com.google.firebase.messaging.FirebaseMessaging;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserService userService;
    private final SimpMessagingTemplate messagingTemplate;

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
            log.info("Successfully sent push notification to token");
        } catch (Exception e) {
            log.error("Failed to send FCM push notification: {}", e.getMessage());
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

        try {
            messagingTemplate.convertAndSendToUser(recipientId.toString(), "/queue/notifications", mapToResponse(savedNotification));
        } catch (Exception e) {
            log.error("Failed to send WebSocket notification: {}", e.getMessage());
        }

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
