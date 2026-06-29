package com.example.service;

import com.example.entity.RefreshToken;
import com.example.entity.User;

public interface RefreshTokenService {
    
    RefreshToken createRefreshToken(User user);
    
    RefreshToken verifyExpiration(RefreshToken token);
    
    RefreshToken findByToken(String token);

    void deleteByToken(String token);
}
