package com.example.service;

import com.example.dto.OAuth2Request;
import com.example.dto.AuthResponse;

public interface OAuth2Service {
    
    AuthResponse loginWithGoogle(OAuth2Request request);
}
