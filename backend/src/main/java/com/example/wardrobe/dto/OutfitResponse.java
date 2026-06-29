package com.example.wardrobe.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * Outfit Response DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OutfitResponse {
    
    private Long id;
    private String name;
    private String description;
    private String occasion;
    private String season;
    private String previewImageUrl;
    private Set<WardrobeItemResponse> items;
    private Double rating;
    private boolean favorite;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
