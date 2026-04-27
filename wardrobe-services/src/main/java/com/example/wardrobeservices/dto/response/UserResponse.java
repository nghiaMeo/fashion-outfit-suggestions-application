package com.example.wardrobeservices.dto.response;

import com.example.wardrobeservices.entity.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
    private UUID id;
    private String email;
    private String username;
    private String avatarUrl;
    private String bio;
    private Role role;
    private Instant createdAt;
}
