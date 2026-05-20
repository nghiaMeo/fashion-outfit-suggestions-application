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

    long countByUserIdAndIsDeletedFalse(UUID userId);

    List<Outfit> findByUserIdAndIsDeletedFalse(UUID userId);

    @Query("SELECT o FROM Outfit o WHERE o.userId = :userId AND o.isDeleted = false " +
            "AND (:occasion IS NULL OR :occasion = '' OR LOWER(o.occasion) LIKE LOWER(CONCAT('%', :occasion, '%')))" +
            "AND (:name IS NULL OR :name = '' OR LOWER(o.name) LIKE LOWER(CONCAT('%', :name, '%')))" +
            "AND (:isFavorite IS NULL OR o.isFavorite = :isFavorite )" )
    List<Outfit> searchOutfits(
            @Param("userId") UUID userId,
            @Param("name") String name,
            @Param("occasion") String occasion,
            @Param("isFavorite") Boolean isFavorite
    );

    List<Outfit> findByUserIdInAndIsPublicTrueAndIsDeletedFalseOrderByCreatedAtDesc(List<UUID> userIds);
}

