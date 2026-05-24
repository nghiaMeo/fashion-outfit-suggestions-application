package com.example.controller;

import com.example.dto.request.MessageRequest;
import com.example.dto.response.ApiResponse;
import com.example.dto.response.ConversationResponse;
import com.example.dto.response.MessageResponse;
import com.example.service.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/send")
    public ApiResponse<MessageResponse> sendMessage(@RequestBody @Valid MessageRequest messageRequest) {
        return ApiResponse.<MessageResponse>builder()
                .result(chatService.sendMessage(messageRequest))
                .build();
    }
}
