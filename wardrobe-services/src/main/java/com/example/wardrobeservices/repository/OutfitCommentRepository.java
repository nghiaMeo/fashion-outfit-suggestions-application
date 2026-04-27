package com.example.wardrobeservices.repository;

import com.example.wardrobeservices.entity.OutfitComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OutfitCommentRepository extends JpaRepository<OutfitComment, UUID> {
}
