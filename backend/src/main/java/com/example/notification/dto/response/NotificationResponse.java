package com.example.notification.dto.response;

import com.example.notification.entity.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResponse {
    private UUID id;
    private UUID actorId;
    private String actorName;
    private String actorAvatar;
    private NotificationType type;
    private UUID targetId;
    private String content;
    private boolean isRead;
    private Instant createdAt;
}
