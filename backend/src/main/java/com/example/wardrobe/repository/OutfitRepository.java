package com.example.wardrobe.repository;

import com.example.wardrobe.entity.Outfit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

/**
 * Outfit Repository
 */
@Repository
public interface OutfitRepository extends JpaRepository<Outfit, Long> {
    Page<Outfit> findByUserId(Long userId, Pageable pageable);
    Page<Outfit> findByUserIdAndFavorite(Long userId, boolean favorite, Pageable pageable);
    Optional<Outfit> findByIdAndUserId(Long id, Long userId);
    boolean existsByIdAndUserId(Long id, Long userId);
    List<Outfit> findByUserIdAndOccasion(Long userId, String occasion);
}
