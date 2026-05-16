package com.example.wardrobeservices.service.impl;

import com.corundumstudio.socketio.SocketIOServer;
import com.example.wardrobeservices.dto.request.MessageRequest;
import com.example.wardrobeservices.dto.response.ConversationResponse;
import com.example.wardrobeservices.dto.response.MessageResponse;
import com.example.wardrobeservices.entity.ChatConversation;
import com.example.wardrobeservices.entity.ConversationMember;
import com.example.wardrobeservices.entity.Message;
import com.example.wardrobeservices.entity.User;
import com.example.wardrobeservices.entity.enums.MessageType;
import com.example.wardrobeservices.exception.AppException;
import com.example.wardrobeservices.exception.ErrorCode;
import com.example.wardrobeservices.repository.*;
import com.example.wardrobeservices.service.ChatService;
import com.example.wardrobeservices.service.NotificationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatServiceImpl implements ChatService {

    private final ConversationMemberRepository conversationMemberRepository;
    private final MessageRepository messageRepository;
    private final SocketIOServer socketIOServer;
    private final ChatConversationRepository chatConversationRepository;
    private final OutfitRepository outfitRepository;
    private final NotificationService notificationService;
    private final UserRepository userRepository;

    private User getCurrentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @Override
    public List<ConversationResponse> getMyConversations() {
        var currentUser = getCurrentUser();
        var myMemberships = conversationMemberRepository.findByUser(currentUser);

        return myMemberships.stream()
                .map(m -> mapToConversationResponse(m.getConversation(), currentUser))
                .toList();
    }

    @Override
    public Page<MessageResponse> getMessageHistory(UUID conversationId, Pageable pageable) {
        var currentUser = getCurrentUser();
        var member = conversationMemberRepository.findByConversationIdAndUserId(conversationId, currentUser.getId())
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHORIZED)); // Dùng Unauthorized đúng hơn

        var messages = messageRepository.findByConversationIdOrderByCreatedAtDesc(conversationId, pageable);

        member.setLastReadAt(Instant.now());
        conversationMemberRepository.save(member);
        return messages.map(this::mapToMessageResponse);
    }

    @Override
    public MessageResponse sendMessage(MessageRequest request) {
        var currentUser = getCurrentUser();
        var conversation = chatConversationRepository.findById(request.getConversationId())
                .orElseThrow(() -> new AppException(ErrorCode.CONVERSATION_NOT_FOUND));

        var member = conversationMemberRepository.findByConversationIdAndUserId(conversation.getId(), currentUser.getId())
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHORIZED));

        if (request.getType() == MessageType.OUTFIT_SHARE && request.getSharedOutfitId() != null) {
            var outfit = outfitRepository.findById(request.getSharedOutfitId())
                    .orElseThrow(() -> new AppException(ErrorCode.OUTFIT_NOT_FOUND));

            if (!outfit.getUser().getId().equals(currentUser.getId())) {
                throw new AppException(ErrorCode.UNAUTHORIZED);
            }
            if (request.getContent() == null || request.getContent().isBlank()) {
                request.setContent("I've shared an outfit: " + outfit.getName());
            }
        }

        var message = Message.builder()
                .conversation(conversation)
                .sender(currentUser)
                .content(request.getContent())
                .type(request.getType())
                .imageUrl(request.getImageUrl())
                .sharedOutfitId(request.getSharedOutfitId())
                .build();

        messageRepository.save(message);

        conversation.setLastMessage(request.getContent());
        conversation.setLastMessageAt(Instant.now());
        chatConversationRepository.save(conversation);

        member.setLastReadAt(Instant.now());
        conversationMemberRepository.save(member);

        var response = mapToMessageResponse(message);
        socketIOServer.getRoomOperations(conversation.getId().toString()).sendEvent("new_message", response);

        conversation.getMembers().stream()
                .map(ConversationMember::getUser)
                .filter(u -> !u.getId().equals(currentUser.getId()))
                .forEach(receiver -> {
                    if (receiver.getFcmToken() != null) {
                        notificationService.sendPushNotification(receiver.getFcmToken(), currentUser.getDisplayName(), request.getContent());
                    }
                });

        return response;
    }

    @Override
    public ConversationResponse createConversation(UUID friendId) {
        User currentUser = getCurrentUser();
        User friend = userRepository.findById(friendId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        var conversation = ChatConversation.builder().build();
        chatConversationRepository.save(conversation);

        conversationMemberRepository.save(new ConversationMember(null, conversation, currentUser, null, Instant.now(), null, false));
        conversationMemberRepository.save(new ConversationMember(null, conversation, friend, null, Instant.now(), null, false));

        return mapToConversationResponse(conversation, currentUser);
    }


    private ConversationResponse mapToConversationResponse(ChatConversation conv, User currentUser) {
        var friend = conv.getMembers().stream()
                .map(ConversationMember::getUser)
                .filter(u -> !u.getId().equals(currentUser.getId()))
                .findFirst()
                .orElse(null);

        var unreadCount = messageRepository.countUnreadMessages(conv.getId(), currentUser.getId(),
                conv.getMembers().stream().filter(m -> m.getUser().getId().equals(currentUser.getId()))
                        .findFirst().map(ConversationMember::getLastReadAt).orElse(null));

        return ConversationResponse.builder()
                .conversationId(conv.getId())
                .friendId(friend != null ? friend.getId() : null)
                .friendName(friend != null ? friend.getDisplayName() : "Unknown")
                .friendAvatar(friend != null ? friend.getAvatarUrl() : null)
                .lastMessage(conv.getLastMessage())
                .lastMessageAt(conv.getLastMessageAt())
                .unreadCount(unreadCount)
                .build();
    }

    private MessageResponse mapToMessageResponse(Message m) {
        return MessageResponse.builder()
                .id(m.getId())
                .senderId(m.getSender().getId())
                .senderName(m.getSender().getDisplayName())
                .content(m.getContent())
                .type(m.getType())
                .imageUrl(m.getImageUrl())
                .sharedOutfitId(m.getSharedOutfitId())
                .createdAt(m.getCreatedAt())
                .readAt(m.getReadAt())
                .build();
    }
}
