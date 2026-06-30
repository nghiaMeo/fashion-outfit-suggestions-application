package com.example.social.service.impl;

import com.example.social.dto.response.FriendResponse;
import com.example.user.dto.response.UserSearchResponse;
import com.example.user.dto.response.UserProfileResponse;
import com.example.social.entity.Friendship;
import com.example.user.entity.User;
import com.example.social.entity.enums.FriendshipStatus;
import com.example.notification.entity.enums.NotificationType;
import com.example.common.exception.AppException;
import com.example.common.exception.ErrorCode;
import com.example.social.repository.FriendshipRepository;
import com.example.notification.repository.NotificationRepository;
import com.example.user.repository.UserRepository;
import com.example.social.service.FriendshipService;
import com.example.user.service.UserService;
import com.example.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FriendshipServiceImpl implements FriendshipService {

    private final FriendshipRepository friendshipRepository;
    private final UserService userService;
    private final NotificationService notificationService;
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    private User getCurrentUser() {
        return (User) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();
    }

    @Override
    public String sendFriendRequest(UUID receiverId) {
        var currentUser = getCurrentUser();
        if (currentUser.getId().equals(receiverId)) {
            throw new AppException(ErrorCode.CANNOT_FRIEND_SELF);
        }
        if (!userRepository.existsById(receiverId)) {
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
            notificationService.sendNotification(
                    receiverId,
                    currentUser.getId(),
                    NotificationType.FRIEND_REQUEST,
                    currentUser.getId(),
                    content
            );
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
        processAcceptFriendship(friendship, currentUser);
        return "friendships have been accepted";
    }

    @Override
    @Transactional
    public String declineOrCancelRequest(UUID friendshipId) {
        var currentUser = getCurrentUser();
        var friendship = friendshipRepository.findById(friendshipId)
                .orElseThrow(() -> new AppException(ErrorCode.FRIEND_REQUEST_NOT_FOUND));

        if (!friendship.getRequesterId().equals(currentUser.getId()) && !friendship.getReceiverId().equals(currentUser.getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        deleteFriendRequestNotification(friendship, currentUser);
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
                profiles = userService.getUsersProfiles(requesterIds);
            } catch (Exception e) {
                // Ignore
            }
        }

        Map<UUID, UserProfileResponse> profileMap = profiles.stream()
                .collect(Collectors.toMap(UserProfileResponse::getId, p -> p, (p1, p2) -> p1));

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
                profiles = userService.getUsersProfiles(friendIds);
            } catch (Exception e) {
                // Ignore
            }
        }

        Map<UUID, UserProfileResponse> profileMap = profiles.stream()
                .collect(Collectors.toMap(UserProfileResponse::getId, p -> p, (p1, p2) -> p1));

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
            users = userService.searchUsers(query, currentUser.getId());
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
                    .friendshipStatus(relation.map(f -> {
                        // Map social status enum to user response's representation
                        return com.example.social.entity.enums.FriendshipStatus.valueOf(f.getStatus().name());
                    }).orElse(null))
                    .friendshipId(relation.map(Friendship::getId).orElse(null))
                    .build();
        }).toList();
    }

    @Override
    public List<UUID> getFriendIds() {
        var currentUser = getCurrentUser();

        var friendships = friendshipRepository.findAllAcceptedFriendships(currentUser.getId());

        return friendships.stream().map(f -> f.getRequesterId().equals(currentUser.getId())
                ? f.getReceiverId() : f.getRequesterId()
        ).toList();
    }

    @Override
    @Transactional
    public String unfriendOrCancelByUserId(UUID targetUserId) {
        var currentUser = getCurrentUser();
        var friendship = friendshipRepository.findRelation(currentUser.getId(), targetUserId)
                .orElseThrow(() -> new AppException(ErrorCode.FRIEND_REQUEST_NOT_FOUND));

        deleteFriendRequestNotification(friendship, currentUser);
        friendshipRepository.delete(friendship);
        return "Friendship or request cancelled successfully";
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

    @Override
    @Transactional
    public String acceptFriendRequestByRequesterId(UUID requesterId) {
        var currentUser = getCurrentUser();
        var friendship = friendshipRepository.findRelation(requesterId, currentUser.getId())
                .orElseThrow(() -> new AppException(ErrorCode.FRIEND_REQUEST_NOT_FOUND));
        processAcceptFriendship(friendship, currentUser);
        return "friendships have been accepted";
    }

    private void deleteFriendRequestNotification(Friendship friendship, User currentUser) {
        if (friendship.getStatus() == FriendshipStatus.PENDING) {
            if (friendship.getReceiverId().equals(currentUser.getId())) {
                notificationRepository.deleteByRecipientIdAndActorIdAndType(
                        currentUser.getId(),
                        friendship.getRequesterId(),
                        NotificationType.FRIEND_REQUEST
                );
            } else {
                notificationRepository.deleteByRecipientIdAndActorIdAndType(
                        friendship.getReceiverId(),
                        currentUser.getId(),
                        NotificationType.FRIEND_REQUEST
                );
            }
        }
    }

    private void processAcceptFriendship(Friendship friendship, User currentUser) {
        if (!friendship.getReceiverId().equals(currentUser.getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        if (friendship.getStatus() == FriendshipStatus.ACCEPTED) {
            notificationRepository.deleteByRecipientIdAndActorIdAndType(
                    currentUser.getId(),
                    friendship.getRequesterId(),
                    NotificationType.FRIEND_REQUEST
            );
            return;
        }

        if (friendship.getStatus() != FriendshipStatus.PENDING) {
            throw new AppException(ErrorCode.FRIEND_REQUEST_NOT_FOUND);
        }

        friendship.setStatus(FriendshipStatus.ACCEPTED);
        friendship.setUpdatedAt(Instant.now());
        friendshipRepository.save(friendship);

        notificationRepository.deleteByRecipientIdAndActorIdAndType(
                currentUser.getId(),
                friendship.getRequesterId(),
                NotificationType.FRIEND_REQUEST
        );

        var content = currentUser.getDisplayName() + " has accepted your friend request";
        try {
            notificationService.sendNotification(
                    friendship.getRequesterId(),
                    currentUser.getId(),
                    NotificationType.FRIEND_ACCEPT,
                    currentUser.getId(),
                    content
            );
        } catch (Exception e) {
            // Non-blocking
        }
    }
}
