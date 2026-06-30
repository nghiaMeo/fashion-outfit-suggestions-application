package com.example.notification.dto.request;

import com.example.notification.entity.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationRequest {
    private UUID recipientId;
    private UUID actorId;
    private NotificationType type;
    private UUID targetId;
    private String content;
}
