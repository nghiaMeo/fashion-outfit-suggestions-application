package com.example.wardrobeservices.repository;

import com.example.wardrobeservices.entity.OutfitItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OutfitItemRepository extends JpaRepository<OutfitItem, UUID> {
}
