package com.example.wardrobe.controller;

import com.example.common.dto.ApiResponse;
import com.example.wardrobe.dto.response.OutfitLikeStatusResponse;
import com.example.wardrobe.service.OutfitLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/social/outfits")
@RequiredArgsConstructor
public class OutfitLikeController {
    private final OutfitLikeService outfitLikeService;

    @PostMapping("/{outfitId}/like")
    public ApiResponse<OutfitLikeStatusResponse> toggleLike(@PathVariable("outfitId") UUID outfitId,
                                                            @RequestParam(required = false) UUID ownerId) {
        return ApiResponse.<OutfitLikeStatusResponse>builder()
                .result(outfitLikeService.toggleLike(outfitId, ownerId))
                .build();
    }

    @GetMapping("/{outfitId}/like-status")
    public ApiResponse<OutfitLikeStatusResponse> getLikeStatus(@PathVariable("outfitId") UUID outfitId) {
        return ApiResponse.<OutfitLikeStatusResponse>builder()
                .result(outfitLikeService.getLikeStatus(outfitId))
                .build();
    }

    @PostMapping("/likes-batch")
    public ApiResponse<Map<UUID, OutfitLikeStatusResponse>> getLikesBatch(@RequestBody List<UUID> outfitIds) {
        return ApiResponse.<Map<UUID, OutfitLikeStatusResponse>>builder()
                .result(outfitLikeService.getLikesBatch(outfitIds))
                .build();
    }
}
