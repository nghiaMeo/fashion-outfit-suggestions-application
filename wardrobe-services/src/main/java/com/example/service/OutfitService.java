package com.example.service;


import com.example.dto.OutfitRequest;
import com.example.dto.OutfitResponse;

import java.util.List;
import java.util.UUID;

public interface OutfitService {

    OutfitResponse createOutfit(OutfitRequest outfitRequest);

    List<OutfitResponse> getAllOutfits();

    OutfitResponse toggleFavorite(UUID id);

    OutfitResponse toggleVisibility(UUID id);

    OutfitResponse getOutfitById(UUID id);

    void deleteOutfit(UUID id);

    OutfitResponse updateOutfit(UUID id, OutfitRequest outfitRequest);

    List<OutfitResponse> searchOutfits(String name, String occasion, Boolean isFavorite);

    OutfitResponse toggleLike(UUID id);

    List<OutfitResponse> getHomeFeed();
}
