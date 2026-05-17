package com.example.service.impl;

import com.example.client.NotificationClient;
import com.example.client.UserClient;
import com.example.dto.request.NotificationRequest;
import com.example.dto.response.FriendResponse;
import com.example.dto.response.UserSearchResponse;
import com.example.dto.response.UserProfileResponse;
import com.example.entity.Friendship;
import com.example.entity.User;
import com.example.entity.enums.FriendshipStatus;
import com.example.entity.enums.NotificationType;
import com.example.exception.AppException;
import com.example.exception.ErrorCode;
import com.example.repository.FriendshipRepository;
import com.example.service.FriendshipService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FriendshipServiceImpl implements FriendshipService {

    private final FriendshipRepository friendshipRepository;
    private final UserClient userClient;
    private final NotificationClient notificationClient;

    private User getCurrentUser() {
        return (User) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();
    }

    @Override
    public String sendFriendRequest(UUID receiverId) {
        var currentUser = getCurrentUser();

        if (currentUser.getId().equals(receiverId)) {
            throw new AppException(ErrorCode.CANNOT_FRIEND_SELF);
        }

        UserProfileResponse receiver;
        try {
            receiver = userClient.getProfile(receiverId).getResult();
        } catch (Exception e) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }

        if (receiver == null) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }

        if (friendshipRepository.findRelation(currentUser.getId(), receiverId).isPresent()) {
            throw new AppException(ErrorCode.FRIEND_REQUEST_ALREADY_SENT);
        }

        var friendship = Friendship.builder()
                .requesterId(currentUser.getId())
                .receiverId(receiverId)
                .status(FriendshipStatus.PENDING)
                .build();

        friendshipRepository.save(friendship);

        var content = currentUser.getDisplayName() + " has sent you a friend request";
        try {
            notificationClient.sendNotification(NotificationRequest.builder()
                    .recipientId(receiverId)
                    .actorId(currentUser.getId())
                    .type(NotificationType.FRIEND_REQUEST)
                    .targetId(currentUser.getId())
                    .content(content)
                    .build());
        } catch (Exception e) {
            // Non-blocking notification fail
        }

        return "Request has been sent";
    }

    @Override
    @Transactional
    public String acceptFriendRequest(UUID friendshipId) {
        var currentUser = getCurrentUser();

        var friendship = friendshipRepository.findById(friendshipId)
                .orElseThrow(() -> new AppException(ErrorCode.FRIEND_REQUEST_NOT_FOUND));

        if (!friendship.getReceiverId().equals(currentUser.getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        friendship.setStatus(FriendshipStatus.ACCEPTED);
        friendship.setUpdatedAt(Instant.now());
        friendshipRepository.save(friendship);

        var content = currentUser.getDisplayName() + " has accepted your friend request";
        try {
            notificationClient.sendNotification(NotificationRequest.builder()
                    .recipientId(friendship.getRequesterId())
                    .actorId(currentUser.getId())
                    .type(NotificationType.FRIEND_ACCEPT)
                    .targetId(currentUser.getId())
                    .content(content)
                    .build());
        } catch (Exception e) {
            // Non-blocking
        }

        return "friendships have been accepted";
    }

    @Override
    public String declineOrCancelRequest(UUID friendshipId) {
        var currentUser = getCurrentUser();

        var friendship = friendshipRepository.findById(friendshipId)
                .orElseThrow(() -> new AppException(ErrorCode.FRIEND_REQUEST_NOT_FOUND));

        if (!friendship.getRequesterId().equals(currentUser.getId()) && !friendship.getReceiverId().equals(currentUser.getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        friendshipRepository.delete(friendship);
        return "request accept have been declined";
    }

    @Override
    public List<FriendResponse> getPendingRequests() {
        var currentUser = getCurrentUser();
        var requests = friendshipRepository.findByReceiverIdAndStatus(currentUser.getId(), FriendshipStatus.PENDING);

        List<UUID> requesterIds = requests.stream().map(Friendship::getRequesterId).toList();
        List<UserProfileResponse> profiles = List.of();
        if (!requesterIds.isEmpty()) {
            try {
                profiles = userClient.getUsersProfiles(requesterIds).getResult();
            } catch (Exception e) {
                // Ignore
            }
        }

        java.util.Map<UUID, UserProfileResponse> profileMap = profiles.stream()
                .collect(java.util.stream.Collectors.toMap(UserProfileResponse::getId, p -> p, (p1, p2) -> p1));

        return requests.stream().map(f -> {
            UserProfileResponse friendProfile = profileMap.get(f.getRequesterId());
            return mapToFriendResponse(f, friendProfile, f.getRequesterId());
        }).toList();
    }

    @Override
    public List<FriendResponse> getMyFriends() {
        var currentUser = getCurrentUser();
        var friendships = friendshipRepository.findAllAcceptedFriendships(currentUser.getId());

        List<UUID> friendIds = friendships.stream()
                .map(f -> f.getRequesterId().equals(currentUser.getId()) ? f.getReceiverId() : f.getRequesterId())
                .toList();

        List<UserProfileResponse> profiles = List.of();
        if (!friendIds.isEmpty()) {
            try {
                profiles = userClient.getUsersProfiles(friendIds).getResult();
            } catch (Exception e) {
                // Ignore
            }
        }

        java.util.Map<UUID, UserProfileResponse> profileMap = profiles.stream()
                .collect(java.util.stream.Collectors.toMap(UserProfileResponse::getId, p -> p, (p1, p2) -> p1));

        return friendships.stream().map(f -> {
            UUID friendId = f.getRequesterId().equals(currentUser.getId()) ? f.getReceiverId() : f.getRequesterId();
            UserProfileResponse friendProfile = profileMap.get(friendId);
            return mapToFriendResponse(f, friendProfile, friendId);
        }).toList();
    }

    @Override
    public List<UserSearchResponse> searchUsers(String query) {
        var currentUser = getCurrentUser();
        List<UserProfileResponse> users;
        try {
            users = userClient.searchUsers(query, currentUser.getId()).getResult();
        } catch (Exception e) {
            return List.of();
        }

        return users.stream().map(u -> {
            var relation = friendshipRepository.findRelation(currentUser.getId(), u.getId());
            return UserSearchResponse.builder()
                    .id(u.getId())
                    .username(u.getUsername())
                    .displayName(u.getDisplayName())
                    .avatarUrl(u.getAvatarUrl())
                    .friendshipStatus(relation.map(f -> f.getStatus()).orElse(null))
                    .friendshipId(relation.map(Friendship::getId).orElse(null))
                    .build();
        }).toList();
    }

    private FriendResponse mapToFriendResponse(Friendship friendship, UserProfileResponse friend, UUID friendId) {
        return FriendResponse.builder()
                .id(friendship.getId())
                .friendId(friendId)
                .fullName(friend != null ? friend.getDisplayName() : "Unknown")
                .username(friend != null ? friend.getUsername() : "unknown")
                .avatarUrl(friend != null ? friend.getAvatarUrl() : null)
                .status(friendship.getStatus())
                .build();
    }
}
