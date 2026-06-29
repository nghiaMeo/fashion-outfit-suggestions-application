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
    @Query(value = """
        SELECT c.* FROM chat_conversation c
        WHERE (SELECT COUNT(*) FROM conversation_member m WHERE m.conversation_id = c.id) = 2
        AND EXISTS (SELECT 1 FROM conversation_member m WHERE m.conversation_id = c.id AND m.user_id = CAST(:userId1 AS uuid))
        AND EXISTS (SELECT 1 FROM conversation_member m WHERE m.conversation_id = c.id AND m.user_id = CAST(:userId2 AS uuid))
    """, nativeQuery = true)
    Optional<ChatConversation> findDirectConversation(@Param("userId1") UUID userId1, @Param("userId2") UUID userId2);
}