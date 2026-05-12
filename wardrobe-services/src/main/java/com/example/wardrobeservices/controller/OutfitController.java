package com.example.wardrobeservices.controller;

import com.example.wardrobeservices.dto.request.OutfitRequest;
import com.example.wardrobeservices.dto.response.ApiResponse;
import com.example.wardrobeservices.dto.response.OutfitResponse;
import com.example.wardrobeservices.service.OutfitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/outfits")
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

    @GetMapping("/public/{id}")
    public ApiResponse<OutfitResponse> getPublicOutfit(@PathVariable UUID id) {
        return ApiResponse.<OutfitResponse>builder()
                .result(outfitService.getPublicOutfit(id))
                .build();
    }

    @PatchMapping("/{id}/visibility")
    public ApiResponse<OutfitResponse> toggleVisibility(@PathVariable UUID id) {
        return ApiResponse.<OutfitResponse>builder()
                .result(outfitService.toggleVisibility(id))
                .build();
    }

    @GetMapping("/search")
    public ApiResponse<List<OutfitResponse>> searchOutfits(@RequestParam(required = false) String occasion,
                                                           @RequestParam(required = false) Boolean isFavorite) {
        return ApiResponse.<List<OutfitResponse>>builder()
                .result(outfitService.searchOutfits(occasion, isFavorite))
                .build();
    }

}
