package com.example.service;


import com.example.dto.request.OutfitRequest;
import com.example.dto.response.OutfitResponse;

import java.util.List;
import java.util.UUID;

public interface OutfitService {

    OutfitResponse createOutfit(OutfitRequest outfitRequest);

    List<OutfitResponse> getAllOutfits();

    OutfitResponse toggleFavorite(UUID id);

    OutfitResponse toggleVisibility(UUID id);

    OutfitResponse getPublicOutfit(UUID id);

    List<OutfitResponse> searchOutfits(String occasion, Boolean isFavorite);

    OutfitResponse toggleLike(UUID id);

    List<OutfitResponse> getHomeFeed();


}
