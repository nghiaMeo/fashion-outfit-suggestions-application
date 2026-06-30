package com.example.social.dto.response;

import com.example.social.entity.enums.FriendshipStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FriendResponse {
    private UUID id;
    private UUID friendId;
    private String fullName;
    private String username;
    private String avatarUrl;
    private FriendshipStatus status;
}
