package com.example.wardrobe.controller;

import com.example.common.dto.ApiResponse;
import com.example.wardrobe.dto.request.OutfitRequest;
import com.example.wardrobe.dto.response.OutfitResponse;
import com.example.wardrobe.service.OutfitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/outfits")
@RequiredArgsConstructor
public class OutfitController {

    private final OutfitService outfitService;

    @PostMapping("/add")
    public ApiResponse<OutfitResponse> createOutfit(@RequestBody @Valid OutfitRequest outfitRequest) {
        return ApiResponse.<OutfitResponse>builder()
                .result(outfitService.createOutfit(outfitRequest))
                .build();
    }

    @GetMapping("/all-outfit")
    public ApiResponse<List<OutfitResponse>> getAllOutfit() {
        return ApiResponse.<List<OutfitResponse>>builder()
                .result(outfitService.getAllOutfits())
                .build();
    }

    @PatchMapping("/{id}/favorite")
    public ApiResponse<OutfitResponse> favoriteOutfit(@PathVariable UUID id) {
        return ApiResponse.<OutfitResponse>builder()
                .result(outfitService.toggleFavorite(id))
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<OutfitResponse> getOutfitById(@PathVariable UUID id) {
        return ApiResponse.<OutfitResponse>builder()
                .result(outfitService.getOutfitById(id))
                .build();
    }

    @GetMapping("/public/{id}")
    public ApiResponse<OutfitResponse> getPublicOutfit(@PathVariable UUID id) {
        return ApiResponse.<OutfitResponse>builder()
                .result(outfitService.getOutfitById(id))
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteOutfit(@PathVariable UUID id) {
        outfitService.deleteOutfit(id);
        return ApiResponse.<String>builder()
                .result("Outfit deleted successfully")
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<OutfitResponse> updateOutfit(@PathVariable UUID id, @RequestBody @Valid OutfitRequest outfitRequest) {
        return ApiResponse.<OutfitResponse>builder()
                .result(outfitService.updateOutfit(id, outfitRequest))
                .build();
    }

    @PatchMapping("/{id}/visibility")
    public ApiResponse<OutfitResponse> toggleVisibility(@PathVariable UUID id) {
        return ApiResponse.<OutfitResponse>builder()
                .result(outfitService.toggleVisibility(id))
                .build();
    }

    @GetMapping("/search")
    public ApiResponse<List<OutfitResponse>> searchOutfits(@RequestParam(required = false) String name,
                                                           @RequestParam(required = false) String occasion,
                                                           @RequestParam(required = false) Boolean isFavorite) {
        return ApiResponse.<List<OutfitResponse>>builder()
                .result(outfitService.searchOutfits(name, occasion, isFavorite))
                .build();
    }

    @PostMapping("/{id}/like")
    public ApiResponse<OutfitResponse> likeOutfit(@PathVariable UUID id) {
        return ApiResponse.<OutfitResponse>builder()
                .result(outfitService.toggleLike(id))
                .build();
    }

    @GetMapping("/home-feed")
    public ApiResponse<List<OutfitResponse>> getHomeFeed() {
        return ApiResponse.<List<OutfitResponse>>builder()
                .result(outfitService.getHomeFeed())
                .build();
    }
}
