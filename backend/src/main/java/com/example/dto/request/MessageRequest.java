package com.example.dto.request;

import com.example.entity.enums.MessageType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class MessageRequest {
    @NotNull(message = "Id cant null")
    private UUID conversationId;

    @NotBlank(message = "Content cant empty")
    private String content;

    private MessageType type = MessageType.TEXT;

    private String imageUrl;

    private UUID sharedOutfitId;
}
