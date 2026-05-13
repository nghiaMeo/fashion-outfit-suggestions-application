package com.example.wardrobeservices.controller;

import com.example.wardrobeservices.dto.response.ApiResponse;
import com.example.wardrobeservices.dto.response.FriendResponse;
import com.example.wardrobeservices.dto.response.UserSearchResponse;
import com.example.wardrobeservices.service.FriendshipService;
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

    @DeleteMapping("/cancel/{friendshipId}")
    public ApiResponse<String> declineFriendRequest(@PathVariable UUID friendshipId) {
        return ApiResponse.<String>builder()
                .result(friendshipService.declineOrCancelRequest(friendshipId))
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


}
