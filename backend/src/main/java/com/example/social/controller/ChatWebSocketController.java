package com.example.social.controller;

import com.example.common.security.StompUserPrincipal;
import com.example.social.dto.request.TypingEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.typing")
    public void handleTyping(TypingEvent event, Principal principal) {
        if (event == null || event.getConversationId() == null || principal == null) {
            return;
        }

        String userId = principal.getName();
        if (principal instanceof StompUserPrincipal stompUser) {
            userId = stompUser.getUserId().toString();
        }

        var payload = Map.of(
                "conversationId", event.getConversationId().toString(),
                "userId", userId,
                "isTyping", event.isTyping()
        );

        messagingTemplate.convertAndSend(
                "/topic/conversations." + event.getConversationId() + ".typing",
                payload
        );
    }
}
