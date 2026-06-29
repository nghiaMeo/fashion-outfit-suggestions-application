package com.example.dto.response;

import com.example.entity.enums.MessageType;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class MessageResponse {
    private UUID id;
    private UUID senderId;
    private String senderName;
    private String content;
    private MessageType type;
    private String imageUrl;
    private UUID sharedOutfitId;
    private Instant createdAt;
    private Instant readAt;
    private UUID conversationId;
}
