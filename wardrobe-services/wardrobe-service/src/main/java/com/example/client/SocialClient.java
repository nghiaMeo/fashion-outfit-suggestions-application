package com.example.client;

import com.example.dto.response.ApiResponse;
import com.example.dto.response.OutfitLikeStatusResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@FeignClient(name = "social-service")
public interface SocialClient {

    @GetMapping("/api/friendship/friend-ids")
    ApiResponse<List<UUID>> getFriendIds();

    @PostMapping("/api/social/outfits/{outfitId}/like")
    ApiResponse<OutfitLikeStatusResponse> toggleLike(@PathVariable("outfitId") UUID outfitId,
                                                     @RequestParam("ownerId") UUID ownerId);

    @GetMapping("/api/social/outfits/{outfitId}/like-status")
    ApiResponse<OutfitLikeStatusResponse> getLikeStatus(@PathVariable("outfitId") UUID outfitId);

    @PostMapping("/api/social/outfits/like-batch")
    ApiResponse<Map<UUID, OutfitLikeStatusResponse>> getLikesBatch(@RequestBody List<UUID> outfitIds);

}
