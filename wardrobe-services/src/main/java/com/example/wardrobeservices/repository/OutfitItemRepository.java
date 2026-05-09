package com.example.wardrobeservices.repository;

import com.example.wardrobeservices.entity.OutfitItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OutfitItemRepository extends JpaRepository<OutfitItem, UUID> {
}

