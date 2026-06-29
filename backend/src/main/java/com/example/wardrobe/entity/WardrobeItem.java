package com.example.wardrobe.entity;

import com.example.common.dto.BaseEntity;
import com.example.auth.entity.User;
import lombok.*;
import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Wardrobe Item Entity
 * Represents a clothing item in user's wardrobe
 */
@Entity
@Table(name = "wardrobe_items", indexes = {
    @Index(name = "idx_user_id", columnList = "user_id"),
    @Index(name = "idx_item_type", columnList = "item_type")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WardrobeItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ItemType itemType;

    @Column(name = "color")
    private String color;

    @Column(name = "size")
    private String size;

    @Column(name = "brand")
    private String brand;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "purchase_price")
    private Double purchasePrice;

    @Column(name = "purchase_date")
    private LocalDateTime purchaseDate;

    @Column(name = "condition")
    private String condition;

    @Column(name = "is_active", nullable = false)
    private boolean active;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        active = true;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum ItemType {
        TOP, BOTTOM, DRESS, OUTERWEAR, SHOES, ACCESSORIES, OTHERS
    }
}
