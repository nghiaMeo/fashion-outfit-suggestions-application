package com.example.social.dto.request;

import com.example.social.entity.enums.MessageType;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageRequest {
    @NotBlank(message = "Content is mandatory")
    private String content;

    private UUID conversationId;

    private UUID receiverId;

    @Builder.Default
    private MessageType type = MessageType.TEXT;

    private String imageUrl;

    private UUID sharedOutfitId;
}
