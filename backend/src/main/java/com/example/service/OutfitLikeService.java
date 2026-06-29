package com.example.service;

import com.example.dto.response.OutfitLikeStatusResponse;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface OutfitLikeService {

    OutfitLikeStatusResponse toggleLike(UUID outfitId, UUID ownerId);

    OutfitLikeStatusResponse getLikeStatus(UUID outfitId);

    Map<UUID, OutfitLikeStatusResponse> getLikesBatch(List<UUID> outfitIds);
}
