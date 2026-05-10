package com.example.wardrobeservices.repository;

import com.example.wardrobeservices.entity.Item;
import com.example.wardrobeservices.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ItemRepository extends JpaRepository<Item, UUID> {
    Optional<Item> findByUser(User user);

    Optional<Item> findByName(String name);

    Optional<Item> findBySeason(String season);

    Optional<Item> findByBrand(String category);

    Optional<Item> findByType(String category);

    List<Item> findByUserAndIsDeletedFalse(User user);

    Optional<Item> findByIdAndIsDeletedFalse(UUID id);


}
