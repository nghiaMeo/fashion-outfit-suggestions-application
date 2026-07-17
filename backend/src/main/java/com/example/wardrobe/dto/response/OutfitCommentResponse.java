package com.example.wardrobe.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OutfitCommentResponse {

    private UUID id;
    private UUID outfitId;
    private UUID userId;
    private String username;
    private String userAvatar;
    private String content;
    private UUID parentId;
    private long likeCount;
    private boolean isLiked;
    private List<OutfitCommentResponse> replies;
    private Instant createdAt;

}
