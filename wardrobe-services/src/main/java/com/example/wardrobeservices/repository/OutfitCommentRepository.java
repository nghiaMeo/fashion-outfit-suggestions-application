package com.example.wardrobeservices.repository;

import com.example.wardrobeservices.entity.OutfitComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OutfitCommentRepository extends JpaRepository<OutfitComment, UUID> {
}

