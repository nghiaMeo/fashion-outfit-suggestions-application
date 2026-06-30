package com.example.wardrobe.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OutfitLikeStatusResponse {
    private boolean isLiked;
    private long likeCount;
}
