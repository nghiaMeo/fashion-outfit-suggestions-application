package com.example.wardrobeservices.controller;

import com.example.wardrobeservices.dto.response.ApiResponse;
import com.example.wardrobeservices.dto.response.ConversationResponse;
import com.example.wardrobeservices.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
}
