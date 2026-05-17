package com.example.wardrobeservices.service.impl;

import com.example.wardrobeservices.dto.response.FriendResponse;
import com.example.wardrobeservices.dto.response.UserSearchResponse;
import com.example.wardrobeservices.entity.Friendship;
import com.example.wardrobeservices.entity.User;
import com.example.wardrobeservices.entity.enums.FriendshipStatus;
import com.example.wardrobeservices.entity.enums.NotificationType;
import com.example.wardrobeservices.exception.AppException;
import com.example.wardrobeservices.exception.ErrorCode;
import com.example.wardrobeservices.repository.FriendshipRepository;
import com.example.wardrobeservices.repository.UserRepository;
import com.example.wardrobeservices.service.FriendshipService;
import com.example.wardrobeservices.service.NotificationService;
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
    private final UserRepository userRepository;
    private final NotificationService notificationService;


    @Override
    public String sendFriendRequest(UUID receiverId) {
        var currentUser = (User) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();

        if (currentUser.getId().equals(receiverId)) {
            throw new AppException(ErrorCode.CANNOT_FRIEND_SELF);
        }

        var receiver = userRepository.findById(receiverId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (friendshipRepository.findRelation(currentUser, receiver).isPresent()) {
            throw new AppException(ErrorCode.FRIEND_REQUEST_ALREADY_SENT);
        }

        var friendship = Friendship.builder()
                .requester(currentUser)
                .receiver(receiver)
                .status(FriendshipStatus.PENDING)
                .build();

        friendshipRepository.save(friendship);
        var content = currentUser.getDisplayName() + " has been sent to  accept";
        notificationService.sendNotification(receiver,currentUser,
                NotificationType.FRIEND_REQUEST,currentUser.getId(),content);

        return "Request has been sent";
    }

    @Override
    @Transactional
    public String acceptFriendRequest(UUID friendshipId) {
        var currentUser = (User) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();

        var friendship = friendshipRepository.findById(friendshipId).orElseThrow(() -> new AppException(ErrorCode.FRIEND_REQUEST_NOT_FOUND));

        if (!friendship.getReceiver().getId().equals(currentUser.getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        friendship.setStatus(FriendshipStatus.ACCEPTED);
        friendship.setUpdatedAt(Instant.now());
        friendshipRepository.save(friendship);

        var content = currentUser.getDisplayName() + " has been accepted";
        notificationService.sendNotification(
                friendship.getRequester(),
                currentUser,
                NotificationType.FRIEND_ACCEPT,
                currentUser.getId(),content
        );

        return "friendships have been accepted";

    }

    @Override
    public String declineOrCancelRequest(UUID friendshipId) {
        var currentUser = (User) Objects.requireNonNull(SecurityContextHolder
                .getContext().getAuthentication()).getPrincipal();

        var friendship = friendshipRepository.findById(friendshipId)
                .orElseThrow(() -> new AppException(ErrorCode.FRIEND_REQUEST_NOT_FOUND));

        if (!friendship.getRequester().getId().equals(currentUser.getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        friendshipRepository.delete(friendship);
        return "request accept have been declined";
    }

    @Override
    public List<FriendResponse> getPendingRequests() {
        var currentUser = (User) Objects.requireNonNull(SecurityContextHolder
                .getContext().getAuthentication()).getPrincipal();
        var requests = friendshipRepository.findByReceiverAndStatus(currentUser, FriendshipStatus.PENDING);

        return requests.stream().map(f ->
                mapToFriendResponse(f,f.getRequester())
        ).toList();
    }

    @Override
    public List<FriendResponse> getMyFriends() {
        var currentUser =  (User) Objects.requireNonNull(SecurityContextHolder
                .getContext().getAuthentication()).getPrincipal();

        var friendships = friendshipRepository.findAllAcceptedFriendships(currentUser);

        return friendships.stream().map(
                f -> {
                    var friend = f.getRequester().getId().equals(currentUser.getId())
                            ? f.getReceiver()
                            : f.getRequester();
                    return mapToFriendResponse(f,friend);
                }
        ).toList();
    }

    @Override
    public List<UserSearchResponse> searchUsers(String query) {
        var currentUser = (User) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();

        var users = userRepository.searchUsers(query,currentUser.getId());
        return users.stream().map(
                u -> {
                    var relation = friendshipRepository.findRelation(currentUser, u);
                    return UserSearchResponse.builder()
                            .id(u.getId())
                            .username(u.getUsername())
                            .displayName(u.getDisplayName())
                            .avatarUrl(u.getAvatarUrl())
                            .friendshipStatus(FriendshipStatus.valueOf(relation.map(f-> f.getStatus().toString()).orElse(null)))
                            .friendshipId(relation.map(Friendship::getId).orElse(null))
                            .build();
                }
        ).toList();
    }

    private FriendResponse mapToFriendResponse(Friendship friendship, User friend) {
        return FriendResponse.builder()
                .id(friendship.getId())
                .friendId(friend.getId())
                .fullName(friend.getDisplayName())
                .username(friend.getUsername())
                .avatarUrl(friend.getAvatarUrl())
                .status(friendship.getStatus())
                .build();
    }
}
