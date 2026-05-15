package com.example.wardrobeservices.service.impl;

import com.example.wardrobeservices.dto.response.ConversationResponse;
import com.example.wardrobeservices.entity.ChatConversation;
import com.example.wardrobeservices.entity.ConversationMember;
import com.example.wardrobeservices.entity.User;
import com.example.wardrobeservices.repository.ConversationMemberRepository;
import com.example.wardrobeservices.repository.MessageRepository;
import com.example.wardrobeservices.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

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
}
