package com.example.wardrobe.service;

import com.example.wardrobe.dto.request.OutfitRequest;
import com.example.wardrobe.dto.response.OutfitResponse;

import java.util.List;
import java.util.UUID;

public interface OutfitService {
    OutfitResponse createOutfit(OutfitRequest outfitRequest);
    List<OutfitResponse> getAllOutfits();
    OutfitResponse toggleFavorite(UUID id);
    OutfitResponse toggleVisibility(UUID id);
    OutfitResponse getOutfitById(UUID id);
    OutfitResponse getPublicOutfit(UUID id);
    void deleteOutfit(UUID id);
    OutfitResponse updateOutfit(UUID id, OutfitRequest outfitRequest);
    List<OutfitResponse> searchOutfits(String name, String occasion, Boolean isFavorite);
    OutfitResponse toggleLike(UUID id);
    List<OutfitResponse> getHomeFeed();
}
