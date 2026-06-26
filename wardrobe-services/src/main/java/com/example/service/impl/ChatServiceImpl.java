package com.example.service.impl;

import com.corundumstudio.socketio.SocketIOServer;


import com.example.cache.UserProfileCache;
import com.example.dto.request.MessageRequest;
import com.example.dto.response.ConversationResponse;
import com.example.dto.response.MessageResponse;
import com.example.dto.response.UserProfileResponse;
import com.example.entity.ChatConversation;
import com.example.entity.ConversationMember;
import com.example.entity.Message;
import com.example.entity.User;
import com.example.entity.enums.NotificationType;
import com.example.exception.AppException;
import com.example.exception.ErrorCode;
import com.example.repository.ChatConversationRepository;
import com.example.repository.ConversationMemberRepository;
import com.example.repository.MessageRepository;
import com.example.repository.UserRepository;
import com.example.service.ChatService;
import com.example.service.NotificationService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional
public class ChatServiceImpl implements ChatService {

    private final ConversationMemberRepository conversationMemberRepository;
    private final MessageRepository messageRepository;
    private final SocketIOServer socketIOServer;
    private final ChatConversationRepository chatConversationRepository;
    private final NotificationService notificationService;
    private final UserProfileCache userProfileCache;
    private final UserRepository userRepository;

    public ChatServiceImpl(
            ConversationMemberRepository conversationMemberRepository,
            MessageRepository messageRepository,
            @Qualifier("socialSocketIOServer") SocketIOServer socketIOServer,
            ChatConversationRepository chatConversationRepository,
            NotificationService notificationService,
            UserProfileCache userProfileCache, UserRepository userRepository) {
        this.conversationMemberRepository = conversationMemberRepository;
        this.messageRepository = messageRepository;
        this.socketIOServer = socketIOServer;
        this.chatConversationRepository = chatConversationRepository;
        this.notificationService = notificationService;
        this.userProfileCache = userProfileCache;
        this.userRepository = userRepository;
    }

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

        // In microservice architecture, sharedOutfitId is accepted stateless without DB hit to wardrobe-service.
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
        for (var m : conversation.getMembers()) {
            var memberRoom = m.getUserId().toString();
            socketIOServer.getRoomOperations(memberRoom).sendEvent("new_message", response);
            for (var client : socketIOServer.getRoomOperations(memberRoom).getClients()) {
                client.joinRoom(conversation.getId().toString());
            }
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

        // Kiểm tra nhẹ: user có tồn tại không
        if (!userRepository.existsById(friendId)) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }

        // Nếu đã có cuộc trò chuyện rồi, trả về luôn — không tạo trùng
        var existing = chatConversationRepository.findDirectConversation(currentUser.getId(), friendId);
        if (existing.isPresent()) {
            return mapToConversationResponse(existing.get(), currentUser);
        }

        // Tạo cuộc trò chuyện mới
        var conversation = ChatConversation.builder().build();
        conversation = chatConversationRepository.save(conversation);

        var member1 = new ConversationMember(null, conversation, currentUser.getId(), null, Instant.now(), null, false);
        var member2 = new ConversationMember(null, conversation, friendId, null, Instant.now(), null, false);

        conversationMemberRepository.save(member1);
        conversationMemberRepository.save(member2);

        // ĐỒNG BỘ DANH SÁCH MEMBERS cho đối tượng trong bộ nhớ để tránh NullPointerException khi map response
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
