package com.example.wardrobe.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
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
    private String comment;
    private String userAvatar;
    private String content;
    private Instant createdAt;

}
