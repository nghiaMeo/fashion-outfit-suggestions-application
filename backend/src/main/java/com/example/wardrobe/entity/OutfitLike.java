package com.example.wardrobe.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Data
@Table(name = "outfit_likes")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OutfitLike {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "outfit_id", nullable = false)
    private UUID outfitId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Builder.Default
    private Instant createdAt = Instant.now();
}
