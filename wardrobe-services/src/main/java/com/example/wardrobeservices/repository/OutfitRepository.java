package com.example.wardrobeservices.repository;

import com.example.wardrobeservices.entity.Outfit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OutfitRepository extends JpaRepository<Outfit, UUID> {
}
