package com.example.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FriendSuggestionResponse {
    private UUID userId;
    private String username;
    private String displayName;
    private String avatarUrl;
    private long mutualFriendsCount;
    private List<String> matchingStyles;
    private double score;
}
