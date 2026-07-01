package com.example.social.controller;

import com.example.social.dto.request.MessageRequest;
import com.example.common.dto.ApiResponse;
import com.example.social.dto.response.ConversationResponse;
import com.example.social.dto.response.MessageResponse;
import com.example.social.service.ChatService;
import com.example.user.service.UserService;
import com.example.user.repository.UserRepository;
import com.example.user.entity.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.cache.CacheManager;
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
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;
    private final UserRepository userRepository;
    private final UserService userService;
    private final CacheManager cacheManager;

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

    @PostMapping("/conversations/{friendId}")
    public ApiResponse<ConversationResponse> createConversation(@PathVariable UUID friendId) {
        return ApiResponse.<ConversationResponse>builder()
                .result(chatService.createConversation(friendId))
                .build();
    }

    @GetMapping("/debug")
    public String debug() {
        StringBuilder sb = new StringBuilder();
        try {
            List<User> users = userRepository.findAll();
            if (users.isEmpty()) {
                return "No users in DB";
            }
            User dummyUser = users.get(0);
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(dummyUser, null, java.util.Collections.emptyList());
            SecurityContextHolder.getContext().setAuthentication(auth);

            for (User u : users) {
                sb.append("User: ").append(u.getId()).append(" | ").append(u.getUsername()).append(" | ").append(u.getDisplayName()).append("\n");
                try {
                    var profile = userService.getUserProfile(u.getId());
                    sb.append("  Profile: ").append(profile.getDisplayName()).append(" | ").append(profile.getAvatarUrl()).append("\n");
                } catch (Exception e) {
                    sb.append("  Profile Error: ").append(e.getClass().getName()).append(": ").append(e.getMessage()).append("\n");
                    java.io.StringWriter sw = new java.io.StringWriter();
                    e.printStackTrace(new java.io.PrintWriter(sw));
                    sb.append(sw.toString()).append("\n");
                }
            }
        } catch (Exception e) {
            sb.append("Global Error: ").append(e.getMessage());
        }
        return sb.toString();
    }
}
