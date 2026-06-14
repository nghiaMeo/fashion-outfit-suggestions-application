package com.example.controller;

import com.example.dto.ApiResponse;
import com.example.dto.FriendResponse;
import com.example.dto.UserSearchResponse;
import com.example.service.FriendshipService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/friendship")
@RequiredArgsConstructor
public class FriendshipController {
    private final FriendshipService friendshipService;

    @PostMapping("/request/{receiverId}")
    public ApiResponse<String> sendFriendRequest(@PathVariable("receiverId") UUID receiverId) {
        return ApiResponse.<String>builder()
                .result(friendshipService.sendFriendRequest(receiverId))
                .build();
    }

    @PostMapping("/accept/{friendshipId}")
    public ApiResponse<String> acceptFriendship(@PathVariable("friendshipId") UUID friendshipId) {
        return ApiResponse.<String>builder()
                .result(friendshipService.acceptFriendRequest(friendshipId))
                .build();
    }

    @PostMapping("/accept/by-requester/{requesterId}")
    public ApiResponse<String> acceptFriendshipByRequester(@PathVariable("requesterId") UUID requesterId) {
        return ApiResponse.<String>builder()
                .result(friendshipService.acceptFriendRequestByRequesterId(requesterId))
                .build();
    }

    @DeleteMapping("/cancel/{friendshipId}")
    public ApiResponse<String> declineFriendRequest(@PathVariable UUID friendshipId) {
        return ApiResponse.<String>builder()
                .result(friendshipService.declineOrCancelRequest(friendshipId))
                .build();
    }

    @DeleteMapping("/user/{targetUserId}")
    public ApiResponse<String> unfriendOrCancelByUserId(@PathVariable("targetUserId") UUID targetUserId) {
        return ApiResponse.<String>builder()
                .result(friendshipService.unfriendOrCancelByUserId(targetUserId))
                .build();

    }

    @GetMapping("/pending")
    public ApiResponse<List<FriendResponse>> getPendingRequests() {
        return ApiResponse.<List<FriendResponse>>builder()
                .result(friendshipService.getPendingRequests())
                .build();
    }

    @GetMapping("/my-friends")
    public ApiResponse<List<FriendResponse>> getMyFriends() {
        return ApiResponse.<List<FriendResponse>>builder()
                .result(friendshipService.getMyFriends())
                .build();
    }

    @GetMapping("/search-users")
    public ApiResponse<List<UserSearchResponse>> getSearchUsers(@RequestParam String query) {
        return ApiResponse.<List<UserSearchResponse>>builder()
                .result(friendshipService.searchUsers(query))
                .build();
    }

    @GetMapping("/friend-ids")
    public ApiResponse<List<UUID>> getFriendIds() {
        return ApiResponse.<List<UUID>>builder()
                .result(friendshipService.getFriendIds())
                .build();
    }


}
