package com.example.wardrobe.dto;

import com.example.wardrobe.entity.WardrobeItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Create Wardrobe Item DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateWardrobeItemRequest {
    
    @NotBlank(message = "Item name is required")
    private String name;

    @NotNull(message = "Item type is required")
    private WardrobeItem.ItemType itemType;

    private String color;
    private String size;
    private String brand;
    private String imageUrl;
    private String description;
    private Double purchasePrice;
    private String condition;
}
