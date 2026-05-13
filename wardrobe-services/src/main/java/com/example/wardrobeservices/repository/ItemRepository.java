package com.example.wardrobeservices.repository;

import com.example.wardrobeservices.entity.Item;
import com.example.wardrobeservices.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ItemRepository extends JpaRepository<Item, UUID> {

    long countByUser(User user);

    List<Item> findByUserAndIsDeletedFalse(User user);

    Optional<Item> findByIdAndIsDeletedFalse(UUID id);

    @Query("SELECT i FROM Item i WHERE i.user= :user AND i.isDeleted = false " +
            "AND (:name IS NULL OR LOWER(i.name) LIKE LOWER(CONCAT('%', :name, '%')))" +
            "AND (:color IS NULL OR i.color = :color)")
    Page<Item> searchItems(@Param("user") User user,
                           @Param("name") String name,
                           @Param("type") String type,
                           @Param("color") String color,
                           Pageable pageable);

    @Query("SELECT i.type, COUNT(i) FROM Item i WHERE i.user = :user AND i.isDeleted = false GROUP BY i.type")
    List<Objects[]> countItemsGroupByType(@Param("user") User user);

    long countByUserAndIsDeletedFalse(User user);

    Optional<Item> findByIdAndIsDeletedTrue(UUID id);

    List<Item> findByUserAndIsDeletedTrue(User user);

}
