package com.example.user.dto.response;

import com.example.social.entity.enums.FriendshipStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserProfileResponse {
    private UUID id;
    private String username;
    private String displayName;
    private String avatarUrl;
    private String bio;

    private long itemCount;
    private long outfitCount;
    private long friendCount;

    private boolean isPrivateProfile;

    private String favoriteStyles;

    private FriendshipStatus friendshipStatus;
}
