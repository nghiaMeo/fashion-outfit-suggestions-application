package com.example.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Outfit {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private String name;

    private String description;

    private String occasion;

    @Builder.Default
    private boolean isFavorite = false;

    @Builder.Default
    private boolean isAiGenerated = false;

    @Builder.Default
    private double score = 0.0;

    @Builder.Default
    private boolean isDailySuggestion = false;

    @Builder.Default
    private boolean isPublic = true;

    private String suitableWeather;

    @Builder.Default
    private boolean isDeleted = false;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "outfit_items",
            joinColumns = @JoinColumn(name = "outfit_id"),
            inverseJoinColumns = @JoinColumn(name = "item_id")
    )
    private List<Item> items;

    @Builder.Default
    private Instant createdAt = Instant.now();

}
