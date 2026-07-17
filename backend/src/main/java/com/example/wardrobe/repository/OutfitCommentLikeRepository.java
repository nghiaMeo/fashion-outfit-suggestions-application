package com.example.wardrobe.repository;

import com.example.wardrobe.entity.OutfitCommentLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OutfitCommentLikeRepository extends JpaRepository<OutfitCommentLike, UUID> {

    long countByCommentId(UUID commentId);

    boolean existsByCommentIdAndUserId(UUID commentId, UUID userId);

    Optional<OutfitCommentLike> findByCommentIdAndUserId(UUID commentId, UUID userId);
}
