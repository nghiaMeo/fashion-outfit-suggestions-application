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

        var outfits = outfitRepository.findByUserIdAndIsDeletedFalse(currentUser.getId()).stream().toList();

        return outfits.stream().map(this::mapToOutfitResponse).toList();
    }

    @Override
    public OutfitResponse toggleFavorite(UUID id) {
        var currentUser = (User) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();
        var outfit = outfitRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.OUTFIT_NOT_FOUND));

        if (outfit.isDeleted()) {
            throw new AppException(ErrorCode.OUTFIT_NOT_FOUND);
        }

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

        if (outfit.isDeleted()) {
            throw new AppException(ErrorCode.OUTFIT_NOT_FOUND);
        }

        if (!outfit.getUserId().equals(currentUser.getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        outfit.setPublic(!outfit.isPublic());
        var savedOutfit = outfitRepository.save(outfit);
        return mapToOutfitResponse(savedOutfit);
    }

    @Override
    public OutfitResponse getOutfitById(UUID id) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID currentUserId = null;
        if (authentication != null && authentication.getPrincipal() instanceof User user) {
            currentUserId = user.getId();
        }

        var outfit = outfitRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.OUTFIT_NOT_FOUND));

        if (outfit.isDeleted()) {
            throw new AppException(ErrorCode.OUTFIT_NOT_FOUND);
        }

        if (!outfit.isPublic()) {
            if (currentUserId == null || !outfit.getUserId().equals(currentUserId)) {
                throw new AppException(ErrorCode.UNAUTHORIZED);
            }
        }

        return mapToOutfitResponse(outfit);
    }

    @Override
    @Transactional
    public void deleteOutfit(UUID id) {
        var currentUser = (User) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();
        var outfit = outfitRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.OUTFIT_NOT_FOUND));

        if (outfit.isDeleted()) {
            throw new AppException(ErrorCode.OUTFIT_NOT_FOUND);
        }

        if (!outfit.getUserId().equals(currentUser.getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        outfit.setDeleted(true);
        outfitRepository.save(outfit);
    }

    @Override
    @Transactional
    public OutfitResponse updateOutfit(UUID id, OutfitRequest outfitRequest) {
        var currentUser = (User) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();
        var outfit = outfitRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.OUTFIT_NOT_FOUND));

        if (outfit.isDeleted()) {
            throw new AppException(ErrorCode.OUTFIT_NOT_FOUND);
        }

        if (!outfit.getUserId().equals(currentUser.getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        var items = itemRepository.findAllById(outfitRequest.getItemIds()).stream().toList();
        for (Item item : items) {
            if (!item.getUserId().equals(currentUser.getId())) {
                throw new AppException(ErrorCode.UNAUTHORIZED);
            }
        }

        outfit.setName(outfitRequest.getName());
        outfit.setOccasion(outfitRequest.getOccasion());
        outfit.setDescription(outfitRequest.getDescription());
        outfit.setItems(items);

        var savedOutfit = outfitRepository.save(outfit);
        return mapToOutfitResponse(savedOutfit);
    }

    @Override
    public List<OutfitResponse> searchOutfits(String name, String occasion, Boolean isFavorite) {
        var currentUser = (User) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();

        var outfits = outfitRepository.searchOutfits(currentUser.getId(), name, occasion, isFavorite);

        return outfits.stream().map(this::mapToOutfitResponse).toList();
    }

    @Override
    @Transactional
    public OutfitResponse toggleLike(UUID id) {
        // Decoupled mock implementation: Social actions belong in social-service
        var outfit = outfitRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.OUTFIT_NOT_FOUND));
        if (outfit.isDeleted()) {
            throw new AppException(ErrorCode.OUTFIT_NOT_FOUND);
        }
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
        var shareLink = outfit.isPublic() && !outfit.isDeleted()
                ? "http://localhost:8080/api/outfits/public/" + outfit.getId()
                : null;

        return OutfitResponse.builder()
                .id(outfit.getId())
                .name(outfit.getName())
                .occasion(outfit.getOccasion())
                .isAiGenerated(outfit.isAiGenerated())
                .isFavorite(outfit.isFavorite())
                .isPublic(outfit.isPublic())
                .shareLink(shareLink)
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
