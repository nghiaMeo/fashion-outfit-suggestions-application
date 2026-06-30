package com.example.social.dto.response;

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
public class ConversationResponse {
    private UUID conversationId;
    private UUID friendId;
    private String friendName;
    private String friendAvatar;
    private String lastMessage;
    private Instant lastMessageAt;
    private long unreadCount;
}
