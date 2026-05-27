package com.example.service.impl;

import com.example.dto.OutfitRequest;
import com.example.dto.ItemResponse;
import com.example.dto.OutfitLikeStatusResponse;
import com.example.dto.OutfitResponse;
import com.example.entity.Item;
import com.example.entity.Outfit;
import com.example.entity.User;
import com.example.exception.AppException;
import com.example.exception.ErrorCode;
import com.example.repository.ItemRepository;
import com.example.repository.OutfitRepository;
import com.example.service.OutfitService;
import com.example.service.FriendshipService;
import com.example.service.OutfitLikeService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class OutfitServiceImpl implements OutfitService {

    private static final Logger log = LoggerFactory.getLogger(OutfitServiceImpl.class);
    private final OutfitRepository outfitRepository;
    private final ItemRepository itemRepository;
    private final FriendshipService friendshipService;
    private final OutfitLikeService outfitLikeService;

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

        return mapToOutfitResponses(outfits);
    }

    @Override
    public OutfitResponse toggleFavorite(UUID id) {
        Outfit outfit = getValidOutfitForCurrentUser(id);
        outfit.setFavorite(!outfit.isFavorite());
        var savedOutfit = outfitRepository.save(outfit);
        return mapToOutfitResponseWithLike(savedOutfit);
    }

    @Override
    public OutfitResponse toggleVisibility(UUID id) {
        Outfit outfit = getValidOutfitForCurrentUser(id);
        outfit.setPublic(!outfit.isPublic());
        var savedOutfit = outfitRepository.save(outfit);
        return mapToOutfitResponseWithLike(savedOutfit);
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

        return mapToOutfitResponseWithLike(outfit);
    }

    @Override
    @Transactional
    public void deleteOutfit(UUID id) {
        Outfit outfit = getValidOutfitForCurrentUser(id);
        outfit.setDeleted(true);
        outfitRepository.save(outfit);
    }

    @Override
    @Transactional
    public OutfitResponse updateOutfit(UUID id, OutfitRequest outfitRequest) {
        Outfit outfit = getValidOutfitForCurrentUser(id);
        var currentUser = (User) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();

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
        return mapToOutfitResponseWithLike(savedOutfit);
    }

    @Override
    public List<OutfitResponse> searchOutfits(String name, String occasion, Boolean isFavorite) {
        var currentUser = (User) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();

        var outfits = outfitRepository.searchOutfits(currentUser.getId(), name, occasion, isFavorite);
        return mapToOutfitResponses(outfits);
    }

    @Override
    @Transactional
    public OutfitResponse toggleLike(UUID id) {
        var outfit = outfitRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.OUTFIT_NOT_FOUND));
        if (outfit.isDeleted()) {
            throw new AppException(ErrorCode.OUTFIT_NOT_FOUND);
        }
        OutfitLikeStatusResponse likeStatus = null;
        try {
            likeStatus = outfitLikeService.toggleLike(outfit.getId(), outfit.getUserId());
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        var outfitResponse = mapToOutfitResponse(outfit);
        if (likeStatus != null) {
            outfitResponse.setLiked(likeStatus.isLiked());
            outfitResponse.setLikeCount(likeStatus.getLikeCount());
        }
        return outfitResponse;
    }

    @Override
    public List<OutfitResponse> getHomeFeed() {
        List<UUID> friendIds = Collections.emptyList();
        try {
            friendIds = friendshipService.getFriendIds();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        if (friendIds.isEmpty()) {
            return Collections.emptyList();
        }

        List<Outfit> outfits = outfitRepository
                .findByUserIdInAndIsPublicTrueAndIsDeletedFalseOrderByCreatedAtDesc(friendIds);
        return mapToOutfitResponses(outfits);
    }

    private Outfit getValidOutfitForCurrentUser(UUID id) {
        var currentUser = (User) Objects.requireNonNull(
                SecurityContextHolder.getContext().getAuthentication()
        ).getPrincipal();
        var outfit = outfitRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.OUTFIT_NOT_FOUND));
        if (outfit.isDeleted()) {
            throw new AppException(ErrorCode.OUTFIT_NOT_FOUND);
        }
        if (!outfit.getUserId().equals(currentUser.getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        return outfit;
    }

    private OutfitResponse mapToOutfitResponseWithLike(Outfit outfit) {
        var response = mapToOutfitResponse(outfit);
        try {
            var likeStatusRes = outfitLikeService.getLikeStatus(outfit.getId());
            if (likeStatusRes != null) {
                response.setLiked(likeStatusRes.isLiked());
                response.setLikeCount(likeStatusRes.getLikeCount());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    private OutfitResponse mapToOutfitResponse(Outfit outfit) {
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
                .likeCount(0L)
                .isLiked(false)
                .ownerName("Owner")
                .ownerAvatar(null)
                .createdAt(outfit.getCreatedAt())
                .build();
    }

    private List<OutfitResponse> mapToOutfitResponses(List<Outfit> outfits) {
        if (outfits == null || outfits.isEmpty()) {
            return Collections.emptyList();
        }

        var outfitIds = outfits.stream().map(Outfit::getId).toList();
        Map<UUID, OutfitLikeStatusResponse> likesMap = new HashMap<>();
        try {
            likesMap = outfitLikeService.getLikesBatch(outfitIds);
        } catch (Exception e) {
            log.info(e.getMessage());
        }

        Map<UUID, OutfitLikeStatusResponse> finalLikesMap = likesMap;
        return outfits.stream().map(
                outfit -> {
                    var response = mapToOutfitResponse(outfit);
                    var status = finalLikesMap.get(outfit.getId());
                    if (status != null) {
                        response.setLiked(status.isLiked());
                        response.setLikeCount(status.getLikeCount());
                    }
                    return response;
                }).toList();
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
