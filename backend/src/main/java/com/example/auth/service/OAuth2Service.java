package com.example.auth.service;

import com.example.auth.dto.request.OAuth2Request;
import com.example.auth.dto.response.AuthResponse;

public interface OAuth2Service {
    
    AuthResponse loginWithGoogle(OAuth2Request request);
}
