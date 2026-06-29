package com.example.dto.request;

import com.example.entity.Outfit;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
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
public class OutfitRequest {
    @NotBlank(message = "title cannot empty")
    private String name;

    private String occasion;

    private String description;
    private boolean isPublic = true;

    @NotEmpty(message = "must have one item in outfit")
    private List<UUID> items;

    @NotEmpty(message = "must have one item in outfit")
    private List<UUID> itemIds;

}
