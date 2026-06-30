package com.example.auth.service;

import com.example.auth.entity.RefreshToken;
import com.example.user.entity.User;

public interface RefreshTokenService {
    
    RefreshToken createRefreshToken(User user);
    
    RefreshToken verifyExpiration(RefreshToken token);
    
    RefreshToken findByToken(String token);

    void deleteByToken(String token);
}
