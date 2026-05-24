package com.example.service.impl;

import com.corundumstudio.socketio.SocketIOServer;
import com.example.client.NotificationClient;
import com.example.client.UserClient;
import com.example.cache.UserProfileCache;
import com.example.dto.request.MessageRequest;
import com.example.dto.request.NotificationRequest;
import com.example.dto.response.ConversationResponse;
import com.example.dto.response.MessageResponse;
import com.example.dto.response.UserProfileResponse;
import com.example.entity.ChatConversation;
import com.example.entity.ConversationMember;
import com.example.entity.Message;
import com.example.entity.User;
import com.example.entity.enums.MessageType;
import com.example.entity.enums.NotificationType;
import com.example.exception.AppException;
import com.example.exception.ErrorCode;
import com.example.repository.ChatConversationRepository;
import com.example.repository.ConversationMemberRepository;
import com.example.repository.MessageRepository;
import com.example.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatServiceImpl implements ChatService {

    private final ConversationMemberRepository conversationMemberRepository;
    private final MessageRepository messageRepository;
    private final SocketIOServer socketIOServer;
    private final ChatConversationRepository chatConversationRepository;
    private final NotificationClient notificationClient;
    private final UserClient userClient;
    private final UserProfileCache userProfileCache;

    private User getCurrentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @Override
    public List<ConversationResponse> getMyConversations() {
        var currentUser = getCurrentUser();
        var myMemberships = conversationMemberRepository.findByUserId(currentUser.getId());

        List<UUID> friendUserIds = myMemberships.stream()
                .flatMap(m -> m.getConversation().getMembers().stream())
                .map(ConversationMember::getUserId)
                .filter(id -> !id.equals(currentUser.getId()))
                .distinct()
                .toList();

        Map<UUID, UserProfileResponse> profilesMap = new java.util.HashMap<>();
        if (!friendUserIds.isEmpty()) {
            profilesMap = userProfileCache.getProfilesBatch(friendUserIds);
        }

        final var finalProfilesMap = profilesMap;
        return myMemberships.stream()
                .map(m -> mapToConversationResponse(m.getConversation(), currentUser, finalProfilesMap))
                .toList();
    }

    @Override
    public Page<MessageResponse> getMessageHistory(UUID conversationId, Pageable pageable) {
        var currentUser = getCurrentUser();
        var member = conversationMemberRepository.findByConversationIdAndUserId(conversationId, currentUser.getId())
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHORIZED));

        var messages = messageRepository.findByConversationIdOrderByCreatedAtDesc(conversationId, pageable);

        member.setLastReadAt(Instant.now());
        conversationMemberRepository.save(member);

        List<UUID> senderIds = messages.getContent().stream()
                .map(Message::getSenderId)
                .distinct()
                .toList();

        Map<UUID, UserProfileResponse> profilesMap = new java.util.HashMap<>();
        if (!senderIds.isEmpty()) {
            profilesMap = userProfileCache.getProfilesBatch(senderIds);
        }

        final var finalProfilesMap = profilesMap;
        return messages.map(m -> mapToMessageResponse(m, finalProfilesMap));
    }

    @Override
    public MessageResponse sendMessage(MessageRequest request) {
        var currentUser = getCurrentUser();
        var conversation = chatConversationRepository.findById(request.getConversationId())
                .orElseThrow(() -> new AppException(ErrorCode.CONVERSATION_NOT_FOUND));

        var member = conversationMemberRepository.findByConversationIdAndUserId(conversation.getId(), currentUser.getId())
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHORIZED));

        // In microservice architecture, sharedOutfitId is accepted statelessly without DB hit to wardrobe-service.
        var message = Message.builder()
                .conversation(conversation)
                .senderId(currentUser.getId())
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
                .filter(m -> !m.getUserId().equals(currentUser.getId()))
                .forEach(m -> {
                    var content = currentUser.getDisplayName() + " has sent you a message: " + request.getContent();
                    try {
                        notificationClient.sendNotification(NotificationRequest.builder()
                                .recipientId(m.getUserId())
                                .actorId(currentUser.getId())
                                .type(NotificationType.NEW_MESSAGE)
                                .targetId(conversation.getId())
                                .content(content)
                                .build());
                    } catch (Exception e) {
                        // Non-blocking
                    }
                });

        return response;
    }

    @Override
    public ConversationResponse createConversation(UUID friendId) {
        User currentUser = getCurrentUser();
        
        UserProfileResponse friend = userProfileCache.getProfile(friendId);
        if (friend == null) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }

        var conversation = ChatConversation.builder().build();
        chatConversationRepository.save(conversation);

        conversationMemberRepository.save(new ConversationMember(null, conversation, currentUser.getId(), null, Instant.now(), null, false));
        conversationMemberRepository.save(new ConversationMember(null, conversation, friend.getId(), null, Instant.now(), null, false));

        return mapToConversationResponse(conversation, currentUser);
    }

    private ConversationResponse mapToConversationResponse(ChatConversation conv, User currentUser) {
        var friendMember = conv.getMembers().stream()
                .filter(m -> !m.getUserId().equals(currentUser.getId()))
                .findFirst()
                .orElse(null);

        var profilesMap = new java.util.HashMap<UUID, UserProfileResponse>();
        if (friendMember != null) {
            var profile = userProfileCache.getProfile(friendMember.getUserId());
            if (profile != null) {
                profilesMap.put(friendMember.getUserId(), profile);
            }
        }
        return mapToConversationResponse(conv, currentUser, profilesMap);
    }

    private ConversationResponse mapToConversationResponse(ChatConversation conv, User currentUser, java.util.Map<UUID, UserProfileResponse> profilesMap) {
        var friendMember = conv.getMembers().stream()
                .filter(m -> !m.getUserId().equals(currentUser.getId()))
                .findFirst()
                .orElse(null);

        UserProfileResponse friendProfile = null;
        if (friendMember != null) {
            friendProfile = profilesMap.get(friendMember.getUserId());
        }

        var myMember = conv.getMembers().stream()
                .filter(m -> m.getUserId().equals(currentUser.getId()))
                .findFirst()
                .orElse(null);

        var unreadCount = messageRepository.countUnreadMessages(conv.getId(), currentUser.getId(),
                myMember != null ? myMember.getLastReadAt() : null);

        return ConversationResponse.builder()
                .conversationId(conv.getId())
                .friendId(friendProfile != null ? friendProfile.getId() : (friendMember != null ? friendMember.getUserId() : null))
                .friendName(friendProfile != null ? friendProfile.getDisplayName() : "Unknown")
                .friendAvatar(friendProfile != null ? friendProfile.getAvatarUrl() : null)
                .lastMessage(conv.getLastMessage())
                .lastMessageAt(conv.getLastMessageAt())
                .unreadCount(unreadCount)
                .build();
    }

    private MessageResponse mapToMessageResponse(Message m) {
        var profilesMap = new java.util.HashMap<UUID, UserProfileResponse>();
        var profile = userProfileCache.getProfile(m.getSenderId());
        if (profile != null) {
            profilesMap.put(m.getSenderId(), profile);
        }
        return mapToMessageResponse(m, profilesMap);
    }

    private MessageResponse mapToMessageResponse(Message m, java.util.Map<UUID, UserProfileResponse> profilesMap) {
        UserProfileResponse profile = profilesMap.get(m.getSenderId());
        String displayName = profile != null ? profile.getDisplayName() : "Unknown";

        return MessageResponse.builder()
                .id(m.getId())
                .senderId(m.getSenderId())
                .senderName(displayName)
                .content(m.getContent())
                .type(m.getType())
                .imageUrl(m.getImageUrl())
                .sharedOutfitId(m.getSharedOutfitId())
                .createdAt(m.getCreatedAt())
                .readAt(m.getReadAt())
                .build();
    }
}
