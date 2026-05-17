package com.example.wardrobeservices.service.impl;

import com.example.wardrobeservices.dto.request.OutfitRequest;
import com.example.wardrobeservices.dto.response.ItemResponse;
import com.example.wardrobeservices.dto.response.OutfitResponse;
import com.example.wardrobeservices.entity.Item;
import com.example.wardrobeservices.entity.Outfit;
import com.example.wardrobeservices.entity.OutfitLike;
import com.example.wardrobeservices.entity.User;
import com.example.wardrobeservices.entity.enums.NotificationType;
import com.example.wardrobeservices.exception.AppException;
import com.example.wardrobeservices.exception.ErrorCode;
import com.example.wardrobeservices.repository.*;
import com.example.wardrobeservices.service.NotificationService;
import com.example.wardrobeservices.service.OutfitService;
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
    private final OutfitLikeRepository outfitLikeRepository;
    private final NotificationService notificationService;
    private final FriendshipRepository friendshipRepository;

    @Override
    @Transactional
    public OutfitResponse createOutfit(OutfitRequest outfitRequest) {
        var currentUser = (User) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();

        assert currentUser != null;
        var items = itemRepository.findAllById(outfitRequest.getItemIds()).stream().toList();

        for (Item item : items) {
            if (!item.getUser().getId().equals(currentUser.getId())) {
                throw new AppException(ErrorCode.UNAUTHORIZED);
            }
        }

        var outfit = Outfit.builder()
                .name(outfitRequest.getName())
                .occasion(outfitRequest.getOccasion())
                .user(currentUser)
                .items(items)
                .build();

        var savedOutfit = outfitRepository.save(outfit);


        return mapToOutfitResponse(savedOutfit);

    }

    @Override
    public List<OutfitResponse> getAllOutfits() {
        var currentUser = (User) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();

        var outfits = outfitRepository.findByUser(currentUser).stream().toList();

        return outfits.stream().map(this::mapToOutfitResponse).toList();
    }

    @Override
    public OutfitResponse toggleFavorite(UUID id) {
        var currentUser = (User) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();
        var outfit = outfitRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.UNAUTHORIZED));

        if (!outfit.getUser().getId().equals(currentUser.getId())) {
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

        if (!outfit.getUser().getId().equals(currentUser.getId())) {
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

        var outfits = outfitRepository.searchOutfits(currentUser, occasion, isFavorite);

        return outfits.stream().map(this::mapToOutfitResponse).toList();
    }

    @Override
    @Transactional
    public OutfitResponse toggleLike(UUID id) {
        var currentUser = (User) Objects.requireNonNull(SecurityContextHolder
                .getContext().getAuthentication()).getPrincipal();
        var outfit = outfitRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.OUTFIT_NOT_FOUND));

        var existingLike = outfitLikeRepository.findByOutfitIdAndUserId(outfit.getId(), currentUser.getId());

        if (existingLike.isPresent()) {
            outfitLikeRepository.delete(existingLike.get());
        } else {
            var outfitLike = OutfitLike.builder()
                    .outfit(outfit)
                    .user(currentUser)
                    .build();
            outfitLikeRepository.save(outfitLike);

            if (!outfit.getUser().getId().equals(currentUser.getId())) {
                var content = currentUser.getDisplayName() + "has liked your outfit" + outfit.getName();
                notificationService.sendNotification(
                        outfit.getUser(),
                        currentUser,
                        NotificationType.OUTFIT_LIKE,
                        outfit.getId(),
                        content
                );
            }
        }
        return mapToOutfitResponse(outfit);
    }

    @Override
    public List<OutfitResponse> getHomeFeed() {
        var currentUser = (User) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();

        var friendships = friendshipRepository.findAllAcceptedFriendships(currentUser);

        var friends = friendships.stream().map(
                f -> f.getRequester().getId().equals(currentUser.getId()) ? f.getReceiver() : f.getRequester()
        ).toList();

        if (friendships.isEmpty()) {
            return Collections.emptyList();
        }

        var feedOutfits = outfitRepository.findByUserInAndIsPublicTrueOrderByCreatedAtDesc(friends);

        return feedOutfits.stream().map(this::mapToOutfitResponse).toList();
    }


    private OutfitResponse mapToOutfitResponse(Outfit outfit) {

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var isCurrentUserLiked = false;

        if (authentication != null && authentication.getPrincipal() instanceof User currentUser) {
            isCurrentUserLiked = outfitLikeRepository.existsByOutfitIdAndUserId(outfit.getId(), currentUser.getId());
        }

        var totalLikes = outfitLikeRepository.countByOutfitId(outfit.getId());

        return OutfitResponse.builder()
                .id(outfit.getId())
                .name(outfit.getName())
                .occasion(outfit.getOccasion())
                .isAiGenerated(outfit.isAiGenerated())
                .isFavorite(outfit.isFavorite())
                .items(outfit.getItems().stream().map(this::mapToItemResponse).toList())
                .likeCount(totalLikes)
                .isLiked(isCurrentUserLiked)
                .ownerName(outfit.getUser().getDisplayName())
                .ownerAvatar(outfit.getUser().getAvatarUrl())
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
