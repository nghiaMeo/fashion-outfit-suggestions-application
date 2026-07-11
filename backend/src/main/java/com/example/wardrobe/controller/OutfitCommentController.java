package com.example.wardrobe.controller;

import com.example.common.dto.ApiResponse;
import com.example.wardrobe.dto.request.OutfitCommentRequest;
import com.example.wardrobe.dto.response.OutfitCommentResponse;
import com.example.wardrobe.service.OutfitCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/outfits")
@RequiredArgsConstructor
public class OutfitCommentController {

    private final OutfitCommentService outfitCommentService;

    @PostMapping("/{id}/comments")
    public ApiResponse<OutfitCommentResponse> addComment(@PathVariable UUID id, @RequestBody OutfitCommentRequest request) {
        return ApiResponse.<OutfitCommentResponse>builder()
                .result(outfitCommentService.addComment(id, request))
                .build();
    }

    @GetMapping("/{id}/comments")
    public ApiResponse<List<OutfitCommentResponse>> getComments(@PathVariable UUID id) {
        return ApiResponse.<List<OutfitCommentResponse>>builder()
                .result(outfitCommentService.getComments(id))
                .build();
    }

}
