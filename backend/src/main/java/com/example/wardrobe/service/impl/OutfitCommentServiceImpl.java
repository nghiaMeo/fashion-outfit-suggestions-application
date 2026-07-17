package com.example.wardrobe.service.impl;

import com.example.common.exception.AppException;
import com.example.common.exception.ErrorCode;
import com.example.notification.entity.enums.NotificationType;
import com.example.notification.service.NotificationService;
import com.example.user.entity.User;
import com.example.user.repository.UserRepository;
import com.example.wardrobe.dto.request.OutfitCommentRequest;
import com.example.wardrobe.dto.response.OutfitCommentResponse;
import com.example.wardrobe.entity.OutfitComment;
import com.example.wardrobe.entity.OutfitCommentLike;
import com.example.wardrobe.repository.OutfitCommentLikeRepository;
import com.example.wardrobe.repository.OutfitCommentRepository;
import com.example.wardrobe.repository.OutfitRepository;
import com.example.wardrobe.service.OutfitCommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OutfitCommentServiceImpl implements OutfitCommentService {

    private final OutfitCommentRepository outfitCommentRepository;
    private final OutfitRepository outfitRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final OutfitCommentLikeRepository outfitCommentLikeRepository;

    private User getCurrentUser() {
        return (User) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }

    @Override
    public OutfitCommentResponse addComment(UUID outfitId, OutfitCommentRequest request) {
        var currentUser = getCurrentUser();
        var outfit = outfitRepository.findById(outfitId).orElseThrow(
                () -> new AppException(ErrorCode.OUTFIT_NOT_FOUND)
        );
        if (outfit.isDeleted()) {
            throw new AppException(ErrorCode.OUTFIT_NOT_FOUND);
        }

        var comment = OutfitComment.builder()
                .outfitId(outfitId)
                .userId(currentUser.getId())
                .content(request.getContent())
                .parentId(request.getParentId())
                .build();
        var savedComment = outfitCommentRepository.save(comment);

        if (!outfit.getUserId().equals(currentUser.getId())) {
            var notificationContent = currentUser.getUsername() + "commented on your outfit: " + request.getContent();
            try {
                notificationService.sendNotification(
                        outfit.getUserId(),
                        currentUser.getId(),
                        NotificationType.OUTFIT_COMMENT,
                        outfitId,
                        notificationContent
                );
            } catch (Exception e) {
                log.error(e.getMessage());

            }
        }

        return OutfitCommentResponse.builder()
                .id(savedComment.getId())
                .outfitId(savedComment.getOutfitId())
                .userId(savedComment.getUserId())
                .username(currentUser.getUsername())
                .userAvatar(currentUser.getAvatarUrl())
                .content(savedComment.getContent())
                .parentId(savedComment.getParentId())
                .likeCount(0)
                .isLiked(false)
                .replies(Collections.emptyList())
                .createdAt(savedComment.getCreatedAt())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OutfitCommentResponse> getComments(UUID outfitId) {
        var Outfit = outfitRepository.findById(outfitId).orElseThrow(
                () -> new AppException(ErrorCode.OUTFIT_NOT_FOUND)
        );

        if (Outfit.isDeleted()) {
            throw new AppException(ErrorCode.OUTFIT_NOT_FOUND);
        }

        var currentUser = getCurrentUser();
        var rootComments = outfitCommentRepository.findByOutfitIdAndParentIdIsNullOrderByCreatedAtDesc(outfitId);

        return rootComments.stream().map(
                comment -> mapToResponse(comment, currentUser)
        ).toList();
    }

    private OutfitCommentResponse mapToResponse(OutfitComment comment, User currentUser) {
        var userComment = userRepository.findById(comment.getUserId()).orElse(null);

        var username = userComment != null ? userComment.getUsername() : "Unknown";

        var userAvatar = userComment != null ? userComment.getAvatarUrl() : null;

        long likeCount = outfitCommentLikeRepository.countByCommentId(comment.getId());

        boolean isLiked = outfitCommentLikeRepository.existsByCommentIdAndUserId(comment.getId(), currentUser.getId());

        var replyEntities = outfitCommentRepository.findByParentIdOrderByCreatedAtAsc(comment.getId());

        var replies = replyEntities.stream().map(
                reply -> mapToResponse(reply, currentUser)
        ).toList();

        return OutfitCommentResponse.builder()
                .id(comment.getId())
                .outfitId(comment.getOutfitId())
                .userId(comment.getUserId())
                .username(username)
                .userAvatar(userAvatar)
                .content(comment.getContent())
                .parentId(comment.getParentId())
                .createdAt(comment.getCreatedAt())
                .likeCount(likeCount)
                .isLiked(isLiked)
                .replies(replies)
                .build();
    }

    @Override
    public void toggleLikeComment(UUID commentId) {
        var currentUser = getCurrentUser();
        var comment = outfitCommentRepository.findById(commentId)
                .orElseThrow(() -> new AppException(ErrorCode.NOTIFICATION_NOT_FOUND));

        var existingLike = outfitCommentLikeRepository.findByCommentIdAndUserId(commentId, currentUser.getId());

        if (existingLike.isPresent()) {
            outfitCommentLikeRepository.delete(existingLike.get());
        } else {
            var like = OutfitCommentLike.builder()
                    .commentId(commentId)
                    .userId(currentUser.getId())
                    .build();

            outfitCommentLikeRepository.save(like);
        }
    }

}
