package com.example.wardrobe.entity;

import com.example.common.dto.BaseEntity;
import com.example.auth.entity.User;
import lombok.*;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Outfit Entity
 * Represents a combination of wardrobe items that form an outfit
 */
@Entity
@Table(name = "outfits", indexes = {
    @Index(name = "idx_user_id", columnList = "user_id"),
    @Index(name = "idx_occasion", columnList = "occasion")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Outfit extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "occasion")
    private String occasion;

    @Column(name = "season")
    private String season;

    @Column(name = "preview_image_url")
    private String previewImageUrl;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "outfit_items",
        joinColumns = @JoinColumn(name = "outfit_id"),
        inverseJoinColumns = @JoinColumn(name = "item_id")
    )
    @Builder.Default
    private Set<WardrobeItem> items = new HashSet<>();

    @Column(name = "rating")
    private Double rating;

    @Column(name = "is_favorite", nullable = false)
    private boolean favorite;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        favorite = false;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
