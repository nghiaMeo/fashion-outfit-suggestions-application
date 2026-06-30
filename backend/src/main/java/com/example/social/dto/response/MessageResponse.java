package com.example.social.dto.response;

import com.example.social.entity.enums.MessageType;
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
public class MessageResponse {
    private UUID id;
    private UUID senderId;
    private String senderName;
    private String content;
    private MessageType type;
    private String imageUrl;
    private UUID sharedOutfitId;
    private Instant readAt;
    private UUID conversationId;
    private Instant createdAt;
}
