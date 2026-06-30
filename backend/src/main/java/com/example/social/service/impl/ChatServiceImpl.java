package com.example.social.service.impl;

import com.example.common.cache.UserProfileCache;
import com.example.social.dto.request.MessageRequest;
import com.example.social.dto.response.ConversationResponse;
import com.example.social.dto.response.MessageResponse;
import com.example.user.dto.response.UserProfileResponse;
import com.example.social.entity.ChatConversation;
import com.example.social.entity.ConversationMember;
import com.example.social.entity.Message;
import com.example.user.entity.User;
import com.example.notification.entity.enums.NotificationType;
import com.example.common.exception.AppException;
import com.example.common.exception.ErrorCode;
import com.example.social.repository.ChatConversationRepository;
import com.example.social.repository.ConversationMemberRepository;
import com.example.social.repository.MessageRepository;
import com.example.user.repository.UserRepository;
import com.example.social.service.ChatService;
import com.example.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private static final Logger log = LoggerFactory.getLogger(ChatServiceImpl.class);

    private final ConversationMemberRepository conversationMemberRepository;
    private final MessageRepository messageRepository;
    private final ChatConversationRepository chatConversationRepository;
    private final NotificationService notificationService;
    private final UserProfileCache userProfileCache;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

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

        Map<UUID, UserProfileResponse> profilesMap = new HashMap<>();
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

        Map<UUID, UserProfileResponse> profilesMap = new HashMap<>();
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

        try {
            // Send socket message using SimpMessagingTemplate
            messagingTemplate.convertAndSend("/topic/conversations." + conversation.getId(), response);
            for (var m : conversation.getMembers()) {
                messagingTemplate.convertAndSendToUser(m.getUserId().toString(), "/queue/messages", response);
            }
        } catch (Exception e) {
            log.error("Failed to send WebSocket message: {}", e.getMessage());
        }

        conversation.getMembers().stream()
                .filter(m -> !m.getUserId().equals(currentUser.getId()))
                .forEach(m -> {
                    var content = currentUser.getDisplayName() + " has sent you a message: " + request.getContent();
                    try {
                        notificationService.sendNotification(
                                m.getUserId(),
                                currentUser.getId(),
                                NotificationType.NEW_MESSAGE,
                                conversation.getId(),
                                content
                        );
                    } catch (Exception e) {
                        // Non-blocking
                    }
                });

        return response;
    }

    @Override
    public ConversationResponse createConversation(UUID friendId) {
        User currentUser = getCurrentUser();

        if (!userRepository.existsById(friendId)) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }

        var existing = chatConversationRepository.findDirectConversation(currentUser.getId(), friendId);
        if (existing.isPresent()) {
            return mapToConversationResponse(existing.get(), currentUser);
        }

        var conversation = ChatConversation.builder().build();
        conversation = chatConversationRepository.save(conversation);

        var member1 = new ConversationMember(null, conversation, currentUser.getId(), null, Instant.now(), null, false);
        var member2 = new ConversationMember(null, conversation, friendId, null, Instant.now(), null, false);

        conversationMemberRepository.save(member1);
        conversationMemberRepository.save(member2);

        conversation.setMembers(List.of(member1, member2));

        return mapToConversationResponse(conversation, currentUser);
    }

    private ConversationResponse mapToConversationResponse(ChatConversation conv, User currentUser) {
        var friendMember = conv.getMembers().stream()
                .filter(m -> !m.getUserId().equals(currentUser.getId()))
                .findFirst()
                .orElse(null);

        var profilesMap = new HashMap<UUID, UserProfileResponse>();
        if (friendMember != null) {
            var profile = userProfileCache.getProfile(friendMember.getUserId());
            if (profile != null) {
                profilesMap.put(friendMember.getUserId(), profile);
            }
        }
        return mapToConversationResponse(conv, currentUser, profilesMap);
    }

    private ConversationResponse mapToConversationResponse(ChatConversation conv, User currentUser, Map<UUID, UserProfileResponse> profilesMap) {
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
        var lastReadAt = myMember != null ? myMember.getLastReadAt() : null;
        var unreadCount = lastReadAt == null
                ? messageRepository.countByConversationIdAndSenderIdNot(conv.getId(), currentUser.getId())
                : messageRepository.countByConversationIdAndSenderIdNotAndCreatedAtGreaterThan(conv.getId(), currentUser.getId(), lastReadAt);

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
        var profilesMap = new HashMap<UUID, UserProfileResponse>();
        var profile = userProfileCache.getProfile(m.getSenderId());
        if (profile != null) {
            profilesMap.put(m.getSenderId(), profile);
        }
        return mapToMessageResponse(m, profilesMap);
    }

    private MessageResponse mapToMessageResponse(Message m, Map<UUID, UserProfileResponse> profilesMap) {
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
                .conversationId(m.getConversation().getId())
                .build();
    }
}
