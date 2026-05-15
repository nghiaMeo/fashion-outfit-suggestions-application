package com.example.wardrobeservices.controller;

import com.example.wardrobeservices.dto.response.ApiResponse;
import com.example.wardrobeservices.dto.response.ConversationResponse;
import com.example.wardrobeservices.dto.response.MessageResponse;
import com.example.wardrobeservices.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;

    @GetMapping("/conversations")
    public ApiResponse<List<ConversationResponse>> getConversations() {
        return ApiResponse.<List<ConversationResponse>>builder()
                .result(chatService.getMyConversations())
                .build();
    }

    @GetMapping("/conversations/{conversationId}/messages")
    public ApiResponse<Page<MessageResponse>> getMessages(
            @PathVariable UUID conversationId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ApiResponse.<Page<MessageResponse>>builder()
                .result(chatService.getMessageHistory(conversationId, pageable))
                .build();
    }
}
