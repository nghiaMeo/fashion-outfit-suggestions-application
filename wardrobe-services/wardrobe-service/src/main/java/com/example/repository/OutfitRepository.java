package com.example.repository;

import com.example.entity.Outfit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OutfitRepository extends JpaRepository<Outfit, UUID> {

    long countByUserId(UUID userId);

    List<Outfit> findByUserId(UUID userId);

    @Query("SELECT o FROM Outfit o WHERE o.userId = :userId " +
            "AND (:occasion IS NULL OR o.occasion = :occasion)" +
            "AND (:isFavorite IS NULL OR o.isFavorite = :isFavorite )" )
    List<Outfit> searchOutfits(
            @Param("userId") UUID userId,
            @Param("occasion") String occasion,
            @Param("isFavorite") Boolean isFavorite
    );

    List<Outfit> findByUserIdInAndIsPublicTrueOrderByCreatedAtDesc(List<UUID> userIds);
}

