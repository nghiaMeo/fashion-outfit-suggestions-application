package com.example.wardrobeservices.service;

import com.example.wardrobeservices.dto.response.ConversationResponse;

import java.util.List;

public interface ChatService {

    List<ConversationResponse> getMyConversations();
}
