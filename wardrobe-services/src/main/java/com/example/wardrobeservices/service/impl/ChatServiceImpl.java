package com.example.wardrobeservices.service.impl;

import com.example.wardrobeservices.dto.response.ConversationResponse;
import com.example.wardrobeservices.dto.response.MessageResponse;
import com.example.wardrobeservices.entity.ChatConversation;
import com.example.wardrobeservices.entity.ConversationMember;
import com.example.wardrobeservices.entity.User;
import com.example.wardrobeservices.exception.AppException;
import com.example.wardrobeservices.exception.ErrorCode;
import com.example.wardrobeservices.repository.ConversationMemberRepository;
import com.example.wardrobeservices.repository.MessageRepository;
import com.example.wardrobeservices.service.ChatService;
import jakarta.transaction.TransactionScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ConversationMemberRepository conversationMemberRepository;
    private final MessageRepository messageRepository;

    @Override
    public List<ConversationResponse> getMyConversations() {
        var currentUser = (User) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();

        var myMemberships = conversationMemberRepository.findByUser(currentUser);

        return myMemberships.stream().map(myMember -> {
            var conversation = myMember.getConversation();

            var friend = conversation.getMembers().stream()
                    .map(ConversationMember::getUser)
                    .filter(u -> u.getId().equals(currentUser.getId()))
                    .findFirst()
                    .orElse(null);
            var unreadCount = messageRepository.countUnreadMessages(
                    conversation.getId(),
                    currentUser.getId(),
                    myMember.getLastReadAt()
            );

            return ConversationResponse.builder()
                    .conversationId(conversation.getId())
                    .friendId(friend != null ? friend.getId() : null)
                    .friendName(friend != null ? friend.getDisplayName() : "Unknown")
                    .friendAvatar(friend != null ? friend.getAvatarUrl() : null)
                    .lastMessage(conversation.getLastMessage())
                    .lastMessageAt(conversation.getLastMessageAt())
                    .unreadCount(unreadCount)
                    .build();
        }).toList();
    }

    @Override
    @Transactional
    public Page<MessageResponse> getMessageHistory(UUID conversationId, Pageable pageable) {
        var currentUser = (User) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();

        var member = conversationMemberRepository.findByConversationIdAndUserId(conversationId, currentUser.getId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        var messages = messageRepository.findByConversationIdOrderByCreatedAtDesc(conversationId, pageable);

        member.setLastReadAt(Instant.now());
        conversationMemberRepository.save(member);
        return messages.map(m -> MessageResponse.builder()
                .id(m.getId())
                .senderId(m.getSender().getId())
                .senderName(m.getSender().getDisplayName())
                .content(m.getContent())
                .type(m.getType())
                .imageUrl(m.getImageUrl())
                .sharedOutfitId(m.getSharedOutfitId())
                .createdAt(m.getCreatedAt())
                .readAt(m.getReadAt())
                .build());

    }

}
