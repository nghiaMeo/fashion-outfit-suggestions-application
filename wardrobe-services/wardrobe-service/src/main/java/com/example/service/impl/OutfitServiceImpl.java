package com.example.service.impl;

import com.example.dto.request.OutfitRequest;
import com.example.dto.response.ItemResponse;
import com.example.dto.response.OutfitResponse;
import com.example.entity.Item;
import com.example.entity.Outfit;
import com.example.entity.User;
import com.example.exception.AppException;
import com.example.exception.ErrorCode;
import com.example.repository.ItemRepository;
import com.example.repository.OutfitRepository;
import com.example.service.OutfitService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OutfitServiceImpl implements OutfitService {

    private final OutfitRepository outfitRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public OutfitResponse createOutfit(OutfitRequest outfitRequest) {
        var currentUser = (User) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();

        assert currentUser != null;
        var items = itemRepository.findAllById(outfitRequest.getItemIds()).stream().toList();

        for (Item item : items) {
            if (!item.getUserId().equals(currentUser.getId())) {
                throw new AppException(ErrorCode.UNAUTHORIZED);
            }
        }

        var outfit = Outfit.builder()
                .name(outfitRequest.getName())
                .occasion(outfitRequest.getOccasion())
                .userId(currentUser.getId())
                .items(items)
                .build();

        var savedOutfit = outfitRepository.save(outfit);

        return mapToOutfitResponse(savedOutfit);
    }

    @Override
    public List<OutfitResponse> getAllOutfits() {
        var currentUser = (User) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();

        var outfits = outfitRepository.findByUserId(currentUser.getId()).stream().toList();

        return outfits.stream().map(this::mapToOutfitResponse).toList();
    }

    @Override
    public OutfitResponse toggleFavorite(UUID id) {
        var currentUser = (User) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();
        var outfit = outfitRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.OUTFIT_NOT_FOUND));

        if (!outfit.getUserId().equals(currentUser.getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        outfit.setFavorite(!outfit.isFavorite());

        var savedOutfit = outfitRepository.save(outfit);

        return mapToOutfitResponse(savedOutfit);
    }

    @Override
    public OutfitResponse toggleVisibility(UUID id) {
        var currentUser = (User) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();

        var outfit = outfitRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.OUTFIT_NOT_FOUND));

        if (!outfit.getUserId().equals(currentUser.getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        outfit.setPublic(!outfit.isPublic());
        var savedOutfit = outfitRepository.save(outfit);
        return mapToOutfitResponse(savedOutfit);
    }

    @Override
    public OutfitResponse getPublicOutfit(UUID id) {
        var outfit = outfitRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.OUTFIT_NOT_FOUND));

        if (!outfit.isPublic()) {
            throw new AppException(ErrorCode.OUTFIT_PRIVATE);
        }

        return mapToOutfitResponse(outfit);
    }

    @Override
    public List<OutfitResponse> searchOutfits(String occasion, Boolean isFavorite) {
        var currentUser = (User) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();

        var outfits = outfitRepository.searchOutfits(currentUser.getId(), occasion, isFavorite);

        return outfits.stream().map(this::mapToOutfitResponse).toList();
    }

    @Override
    @Transactional
    public OutfitResponse toggleLike(UUID id) {
        // Decoupled mock implementation: Social actions belong in social-service
        var outfit = outfitRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.OUTFIT_NOT_FOUND));
        return mapToOutfitResponse(outfit);
    }

    @Override
    public List<OutfitResponse> getHomeFeed() {
        // Decoupled mock implementation: Social feed aggregation belongs in social-service
        return Collections.emptyList();
    }

    private OutfitResponse mapToOutfitResponse(Outfit outfit) {
        var isCurrentUserLiked = false;
        var totalLikes = 0L;

        return OutfitResponse.builder()
                .id(outfit.getId())
                .name(outfit.getName())
                .occasion(outfit.getOccasion())
                .isAiGenerated(outfit.isAiGenerated())
                .isFavorite(outfit.isFavorite())
                .items(outfit.getItems().stream().map(this::mapToItemResponse).toList())
                .likeCount(totalLikes)
                .isLiked(isCurrentUserLiked)
                .ownerName("Owner") // Will resolve via OpenFeign UserClient later
                .ownerAvatar(null)
                .createdAt(outfit.getCreatedAt())
                .build();
    }

    private ItemResponse mapToItemResponse(Item item) {
        return ItemResponse.builder()
                .id(item.getId())
                .name(item.getName())
                .imageUrl(item.getImageUrl())
                .type(item.getType())
                .color(item.getColor())
                .build();
    }
}
