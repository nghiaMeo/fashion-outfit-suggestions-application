package com.example.service.impl;

import com.example.client.NotificationClient;
import com.example.dto.request.NotificationRequest;
import com.example.dto.response.OutfitLikeStatusResponse;
import com.example.entity.OutfitLike;
import com.example.entity.User;
import com.example.entity.enums.NotificationType;
import com.example.repository.OutfitLikeRepository;
import com.example.service.OutfitLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class OutfitLikeServiceImpl implements OutfitLikeService {
    private final OutfitLikeRepository outfitLikeRepository;
    private final NotificationClient notificationClient;

    private User getCurrentUser() {
        return (User) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();
    }


    @Override
    @Transactional
    public OutfitLikeStatusResponse toggleLike(UUID outfitId, UUID ownerId) {
        var currentUser = getCurrentUser();
        var existingLike = outfitLikeRepository.findByOutfitIdAndUserId(outfitId, currentUser.getId());

        boolean isLiked;

        if (existingLike.isPresent()) {
            outfitLikeRepository.delete(existingLike.get());
            isLiked = false;
        } else {
            var outfitLike = OutfitLike.builder()
                    .outfitId(outfitId)
                    .userId(currentUser.getId())
                    .build();
            outfitLikeRepository.save(outfitLike);
            isLiked = true;

            if (ownerId != null && !currentUser.getId().equals(ownerId)) {
                try {
                    var notificationRequest = NotificationRequest.builder()
                            .recipientId(ownerId)
                            .actorId(currentUser.getId())
                            .type(NotificationType.OUTFIT_LIKE)
                            .targetId(outfitId)
                            .content(currentUser.getDisplayName() + " is liked your outfit")
                            .build();

                    notificationClient.sendNotification(notificationRequest);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        var likeCount = outfitLikeRepository.countByOutfitId(outfitId);

        return OutfitLikeStatusResponse.builder()
                .isLiked(isLiked)
                .likeCount(likeCount)
                .build();
    }

    @Override
    @Transactional
    public OutfitLikeStatusResponse getLikeStatus(UUID outfitId) {
        var currentUser = getCurrentUser();
        var isLiked = outfitLikeRepository.existsByOutfitIdAndUserId(outfitId, currentUser.getId());
        var likeCount = outfitLikeRepository.countByOutfitId(outfitId);

        return OutfitLikeStatusResponse.builder()
                .isLiked(isLiked)
                .likeCount(likeCount)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Map<UUID, OutfitLikeStatusResponse> getLikeStatuses(List<UUID> outfitIds) {
        var currentUser = getCurrentUser();
        Map<UUID, OutfitLikeStatusResponse> result = new HashMap<>();

        if (outfitIds == null && outfitIds.isEmpty()) {
            return result;
        }

        for (UUID outfitId : outfitIds) {
            boolean isLiked = outfitLikeRepository.existsByOutfitIdAndUserId(outfitId, currentUser.getId());
            long likeCount = outfitLikeRepository.countByOutfitId(outfitId);
            result.put(outfitId, OutfitLikeStatusResponse.builder()
                    .isLiked(isLiked)
                    .likeCount(likeCount)
                    .build());
        }

        return result;
    }
}
