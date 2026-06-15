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
                JOIN c.members m1
                JOIN c.members m2
                WHERE m1.userId = :userId1
                AND m2.userId = :userId2
                AND SIZE(c.members) = 2
            """)
    Optional<ChatConversation> findDirectConversation(@Param("userId1") UUID userId1, @Param("userId2") UUID userId2);
}