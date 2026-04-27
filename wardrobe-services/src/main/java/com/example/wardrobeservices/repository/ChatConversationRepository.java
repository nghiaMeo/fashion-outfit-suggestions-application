package com.example.wardrobeservices.repository;

import com.example.wardrobeservices.entity.ChatConversation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ChatConversationRepository extends JpaRepository<ChatConversation, UUID> {
}
