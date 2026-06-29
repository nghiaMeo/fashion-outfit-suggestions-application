package com.example.wardrobe.dto;

import com.example.wardrobe.entity.WardrobeItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Wardrobe Item Response DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WardrobeItemResponse {
    
    private Long id;
    private String name;
    private WardrobeItem.ItemType itemType;
    private String color;
    private String size;
    private String brand;
    private String imageUrl;
    private String description;
    private Double purchasePrice;
    private String condition;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
