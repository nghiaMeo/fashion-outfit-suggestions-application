package com.example.wardrobeservices.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class ConversationResponse {
    private UUID conversationId;

    private UUID friendId;
    private String friendName;
    private String friendAvatar;

    private String lastMessage;
    private Instant lastMessageAt;

    private long unreadCount;
}
