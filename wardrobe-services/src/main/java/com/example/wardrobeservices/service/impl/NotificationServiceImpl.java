package com.example.wardrobeservices.service.impl;

import com.corundumstudio.socketio.SocketIOServer;
import com.example.wardrobeservices.dto.response.NotificationResponse;
import com.example.wardrobeservices.entity.Notification;
import com.example.wardrobeservices.entity.User;
import com.example.wardrobeservices.exception.AppException;
import com.example.wardrobeservices.exception.ErrorCode;
import com.example.wardrobeservices.repository.NotificationRepository;
import com.example.wardrobeservices.entity.enums.NotificationType;
import com.example.wardrobeservices.service.NotificationService;
import com.google.firebase.messaging.FirebaseMessaging;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final SocketIOServer socketIOServer;

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
        var notifications = notificationRepository.findByRecipientOrderByCreatedAtDesc(currentUser);
        
        return notifications.stream().map(this::mapToResponse).toList();
    }

    @Override
    public void markAsRead(UUID notificationId) {
        var currentUser = getCurrentUser();
        var notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new AppException(ErrorCode.NOTIFICATION_NOT_FOUND));

        if (!notification.getRecipient().getId().equals(currentUser.getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        notification.setRead(true);
        notificationRepository.save(notification);
    }

    @Override
    public void markAllAsRead() {
        var currentUser = getCurrentUser();
        var notifications = notificationRepository.findByRecipientOrderByCreatedAtDesc(currentUser);
        
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
        return notificationRepository.countByRecipientAndIsReadFalse(currentUser);
    }

    @Override
    public void sendNotification(User recipient, User actor, NotificationType type, UUID targetId, String content) {
        Notification notification = Notification.builder()
                .recipient(recipient)
                .actor(actor)
                .type(type)
                .targetId(targetId)
                .content(content)
                .build();

        var savedNotification  = notificationRepository.save(notification);

        socketIOServer.getRoomOperations(recipient.getId().toString())
                .sendEvent("new_notification", mapToResponse(savedNotification));

    }


    private NotificationResponse mapToResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .actorId(notification.getActor() != null ? notification.getActor().getId() : null)
                .actorName(notification.getActor() != null ? notification.getActor().getDisplayName() : "System")
                .actorAvatar(notification.getActor() != null ? notification.getActor().getAvatarUrl() : null)
                .type(notification.getType())
                .targetId(notification.getTargetId())
                .content(notification.getContent())
                .isRead(notification.isRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
