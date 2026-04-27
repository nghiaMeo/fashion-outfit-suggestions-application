package com.example.wardrobeservices.entity;

import com.example.wardrobeservices.entity.enums.Role;
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
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String username;

    private String password;

    private String avatarUrl;

    private String bio;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Role role = Role.USER;

    private Instant createdAt = Instant.now();
    private Instant updatedAt = Instant.now();
}
