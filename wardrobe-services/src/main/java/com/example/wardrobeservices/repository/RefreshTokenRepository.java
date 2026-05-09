package com.example.wardrobeservices.repository;

import com.example.wardrobeservices.model.RefreshToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;


@Repository
public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {
    
    Optional<RefreshToken> findByToken(String token);
    
    Optional<RefreshToken> findByUserId(UUID userId);

    void deleteByUserId(UUID userId);

    void deleteByToken(String token);
}
