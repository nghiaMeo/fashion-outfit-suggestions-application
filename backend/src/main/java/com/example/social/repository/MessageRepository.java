package com.example.social.repository;

import com.example.social.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.UUID;

@Repository
public interface MessageRepository extends JpaRepository<Message, UUID> {

    Page<Message> findByConversationIdOrderByCreatedAtDesc(UUID conversationId, Pageable pageable);
    long countByConversationIdAndSenderIdNot(UUID conversationId, UUID senderId);
    long countByConversationIdAndSenderIdNotAndCreatedAtGreaterThan(UUID conversationId, UUID senderId, Instant lastReadAt);
}
