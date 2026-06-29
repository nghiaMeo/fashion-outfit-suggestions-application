package com.example.wardrobe.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.Set;

/**
 * Create Outfit DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateOutfitRequest {
    
    @NotBlank(message = "Outfit name is required")
    private String name;

    private String description;
    private String occasion;
    private String season;
    private String previewImageUrl;

    @NotEmpty(message = "At least one item is required")
    private Set<Long> itemIds;
}
