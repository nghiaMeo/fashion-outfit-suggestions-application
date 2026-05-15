package com.example.wardrobeservices.repository;

import com.example.wardrobeservices.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.UUID;

@Repository
public interface MessageRepository extends JpaRepository<Message, UUID> {

    Page<Message> findByConversationIdOrderByCreatedAtDesc(UUID conversationId, Pageable pageable);

    @Query("SELECT COUNT(m) FROM Message m WHERE m.conversation.id = :conversationId " +
            "AND m.sender.id != :userId " +
            "AND (m.createdAt > :lastReadAt OR :lastReadAt IS NULL)")
    long countUnreadMessages(@Param("conversationId") UUID conversationId,
                             @Param("userId") UUID senderId,
                             @Param("lastReadAt") Instant lastReadAt);
}

