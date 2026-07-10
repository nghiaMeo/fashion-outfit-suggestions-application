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
import com.example.wardrobe.repository.OutfitCommentRepository;
import com.example.wardrobe.repository.OutfitRepository;
import com.example.wardrobe.service.OutfitCommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        var comments = outfitCommentRepository.findByOutfitIdOrderByCreatedAtDesc(outfitId);
        return comments.stream().map(
                comment -> {
                    var userComment = userRepository.findById(comment.getUserId()).orElse(null);
                    var username = userComment != null ? userComment.getUsername() : "Unknown";
                    var userAvatar = userComment != null ? userComment.getAvatarUrl() : null;
                    return OutfitCommentResponse.builder()
                            .id(comment.getId())
                            .outfitId(comment.getOutfitId())
                            .userId(comment.getUserId())
                            .username(username)
                            .userAvatar(userAvatar)
                            .content(comment.getContent())
                            .createdAt(comment.getCreatedAt())
                            .build();
                }
        ).toList();
    }

}
