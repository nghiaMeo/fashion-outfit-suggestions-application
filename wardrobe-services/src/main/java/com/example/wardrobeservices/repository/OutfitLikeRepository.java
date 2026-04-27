package com.example.wardrobeservices.repository;

import com.example.wardrobeservices.entity.OutfitLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OutfitLikeRepository extends JpaRepository<OutfitLike, UUID> {
}
