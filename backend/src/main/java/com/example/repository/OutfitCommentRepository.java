package com.example.repository;

import com.example.entity.OutfitComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OutfitCommentRepository extends JpaRepository<OutfitComment, UUID> {
}
