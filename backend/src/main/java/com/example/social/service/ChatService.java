package com.example.social.service;

import com.example.social.dto.request.MessageRequest;
import com.example.social.dto.response.ConversationResponse;
import com.example.social.dto.response.MessageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface ChatService {

    List<ConversationResponse> getMyConversations();
    Page<MessageResponse> getMessageHistory(UUID conversationId, Pageable pageable);
    MessageResponse sendMessage(MessageRequest request);
    ConversationResponse createConversation(UUID friendId);
}
