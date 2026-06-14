package com.example.service;

import com.example.dto.FriendResponse;
import com.example.dto.UserSearchResponse;

import java.util.List;
import java.util.UUID;

public interface FriendshipService {
    String sendFriendRequest(UUID receiverId);

    String acceptFriendRequest(UUID friendshipId);

    String declineOrCancelRequest(UUID receiverId);

    List<FriendResponse> getPendingRequests();

    List<FriendResponse> getMyFriends();

    List<UserSearchResponse> searchUsers(String query);

    List<UUID> getFriendIds();

    String unfriendOrCancelByUserId(UUID targetUserId);

    String acceptFriendRequestByRequesterId(UUID requesterId);

}
