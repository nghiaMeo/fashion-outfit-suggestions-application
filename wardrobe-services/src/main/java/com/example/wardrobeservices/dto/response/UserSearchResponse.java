package com.example.wardrobeservices.dto.response;

import com.example.wardrobeservices.entity.enums.FriendshipStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserSearchResponse {

    private UUID id;
    private String username;
    private String displayName;
    private String avatarUrl;

    private FriendshipStatus friendshipStatus;

    private UUID friendshipId;
}
