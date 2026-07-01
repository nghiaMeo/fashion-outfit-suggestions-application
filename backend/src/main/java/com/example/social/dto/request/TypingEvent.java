package com.example.social.dto.request;

import lombok.Data;

import java.util.UUID;

@Data
public class TypingEvent {
    private UUID conversationId;
    private boolean typing;
}
