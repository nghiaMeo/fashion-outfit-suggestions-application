package com.example.wardrobeservices.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OutfitResponse {

    private UUID id;
    private String name;
    private String occasion;
    private boolean isFavorite;
    private boolean isAiGenerated;
    private List<ItemResponse> items;
    private Instant createdAt;
}
