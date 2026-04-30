package com.example.wardrobeservices.service;

import com.example.wardrobeservices.dto.request.OAuth2Request;
import com.example.wardrobeservices.dto.response.AuthResponse;

public interface OAuth2Service {
    AuthResponse loginWithGoogle(OAuth2Request request);
}
