package com.example.wardrobeservices.repository;

import com.example.wardrobeservices.entity.RefreshToken;
import com.example.wardrobeservices.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findByToken(String token);
    void deleteByUser(User user);
}
