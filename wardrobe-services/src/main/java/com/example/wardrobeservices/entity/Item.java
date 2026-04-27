package com.example.wardrobeservices.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private String color;

    private String season;

    private String brand;

    private String occasion;

    @Column(columnDefinition = "text")
    private String imageUrl;

    @Column(name = "ai_item_id", nullable = false)
    private UUID aiItemId;

    private Instant createdAt = Instant.now();
    private Instant updatedAt = Instant.now();

}
