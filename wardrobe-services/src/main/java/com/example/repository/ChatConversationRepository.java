package com.example.repository;

import com.example.entity.ChatConversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChatConversationRepository extends JpaRepository<ChatConversation, UUID> {
    @Query("""
                SELECT c FROM ChatConversation c
                WHERE (SELECT COUNT(m) FROM ConversationMember m WHERE m.conversation = c) = 2
                AND EXISTS (SELECT m FROM ConversationMember m WHERE m.conversation = c AND m.userId = :userId1)
                AND EXISTS (SELECT m FROM ConversationMember m WHERE m.conversation = c AND m.userId = :userId2)
            """)
    Optional<ChatConversation> findDirectConversation(@Param("userId1") UUID userId1, @Param("userId2") UUID userId2);
}
