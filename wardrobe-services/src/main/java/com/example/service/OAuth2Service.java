package com.example.service;

import com.example.dto.request.OAuth2Request;
import com.example.dto.response.AuthResponse;

public interface OAuth2Service {
    
    AuthResponse loginWithGoogle(OAuth2Request request);
}
