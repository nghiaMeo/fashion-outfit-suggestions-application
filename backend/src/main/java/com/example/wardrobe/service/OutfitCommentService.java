package com.example.wardrobe.service;

import com.example.wardrobe.dto.request.OutfitCommentRequest;
import com.example.wardrobe.dto.response.OutfitCommentResponse;

import java.util.List;
import java.util.UUID;

public interface OutfitCommentService {
    OutfitCommentResponse addComment(UUID outfitId, OutfitCommentRequest request);

    List<OutfitCommentResponse> getComments(UUID outfitId);

    void toggleLikeComment(UUID commentId);

}
