package com.example.repository;

import com.example.entity.OutfitLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OutfitLikeRepository extends JpaRepository<OutfitLike, UUID> {
    Optional<OutfitLike> findByOutfitIdAndUserId(UUID outfitId, UUID userId);

    long countByOutfitId(UUID outfitId);

    boolean existsByOutfitIdAndUserId(UUID outfitId, UUID userId);
}

