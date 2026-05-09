package com.example.wardrobeservices.service;

import com.example.wardrobeservices.model.RefreshToken;
import com.example.wardrobeservices.entity.User;

public interface RefreshTokenService {
    
    RefreshToken createRefreshToken(User user);
    
    RefreshToken verifyExpiration(RefreshToken token);
    
    RefreshToken findByToken(String token);

    void deleteByToken(String token);
}

