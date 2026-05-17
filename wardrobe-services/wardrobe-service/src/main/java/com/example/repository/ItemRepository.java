package com.example.repository;

import com.example.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ItemRepository extends JpaRepository<Item, UUID> {

    long countByUserId(UUID userId);

    List<Item> findByUserIdAndIsDeletedFalse(UUID userId);

    Optional<Item> findByIdAndIsDeletedFalse(UUID id);

    @Query("SELECT i FROM Item i WHERE i.userId = :userId AND i.isDeleted = false " +
            "AND (:name IS NULL OR LOWER(i.name) LIKE LOWER(CONCAT('%', :name, '%')))" +
            "AND (:color IS NULL OR i.color = :color)")
    Page<Item> searchItems(@Param("userId") UUID userId,
                           @Param("name") String name,
                           @Param("type") String type,
                           @Param("color") String color,
                           Pageable pageable);

    @Query("SELECT i.type, COUNT(i) FROM Item i WHERE i.userId = :userId AND i.isDeleted = false GROUP BY i.type")
    List<Object[]> countItemsGroupByType(@Param("userId") UUID userId);

    long countByUserIdAndIsDeletedFalse(UUID userId);

    Optional<Item> findByIdAndIsDeletedTrue(UUID id);

    List<Item> findByUserIdAndIsDeletedTrue(UUID userId);
}
