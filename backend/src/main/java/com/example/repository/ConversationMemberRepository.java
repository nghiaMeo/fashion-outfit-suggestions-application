package com.example.repository;

import com.example.entity.ConversationMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConversationMemberRepository extends JpaRepository<ConversationMember, UUID> {

    List<ConversationMember> findByUserId(UUID userId);

    List<ConversationMember> findByConversationId(UUID conversationId);

    Optional<ConversationMember> findByConversationIdAndUserId(UUID conversationId, UUID userId);
}
