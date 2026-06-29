package com.example.wardrobe.repository;

import com.example.wardrobe.entity.WardrobeItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

/**
 * Wardrobe Item Repository
 */
@Repository
public interface WardrobeItemRepository extends JpaRepository<WardrobeItem, Long> {
    Page<WardrobeItem> findByUserIdAndActive(Long userId, boolean active, Pageable pageable);
    Page<WardrobeItem> findByUserIdAndItemTypeAndActive(Long userId, WardrobeItem.ItemType itemType, boolean active, Pageable pageable);
    List<WardrobeItem> findByUserIdAndActive(Long userId, boolean active);
    Optional<WardrobeItem> findByIdAndUserId(Long id, Long userId);
    boolean existsByIdAndUserId(Long id, Long userId);
}
