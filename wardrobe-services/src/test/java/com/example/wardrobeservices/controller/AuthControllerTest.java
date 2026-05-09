package com.example.wardrobeservices.controller;

import com.example.wardrobeservices.dto.request.LoginRequest;
import com.example.wardrobeservices.dto.request.OAuth2Request;
import com.example.wardrobeservices.dto.request.RefreshTokenRequest;
import com.example.wardrobeservices.dto.request.UserCreationRequest;
import com.example.wardrobeservices.dto.response.AuthResponse;
import com.example.wardrobeservices.dto.response.UserResponse;
import com.example.wardrobeservices.service.AuthService;
import com.example.wardrobeservices.service.OAuth2Service;
import com.example.wardrobeservices.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @Mock
    private AuthService authService;

    @Mock
    private OAuth2Service oAuth2Service;

    @InjectMocks
    private AuthController authController;

    private ObjectMapper objectMapper = new ObjectMapper();

    private UserCreationRequest userCreationRequest;
    private UserResponse userResponse;
    private LoginRequest loginRequest;
    private AuthResponse authResponse;
    private RefreshTokenRequest refreshTokenRequest;
    private OAuth2Request oAuth2Request;

    @BeforeEach
    void initData() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();

        userCreationRequest = UserCreationRequest.builder()
                .email("test@example.com")
                .username("testuser")
                .displayName("Test User")
                .password("password123")
                .build();

        userResponse = UserResponse.builder()
                .id(UUID.randomUUID())
                .email("test@example.com")
                .username("testuser")
                .displayName("Test User")
                .build();

        loginRequest = LoginRequest.builder()
                .email("test@example.com")
                .password("password123")
                .build();

        authResponse = AuthResponse.builder()
                .accessToken("access-token")
                .refreshToken("refresh-token")
                .tokenType("Bearer")
                .user(userResponse)
                .build();

        refreshTokenRequest = RefreshTokenRequest.builder()
                .refreshToken("refresh-token")
                .build();

        oAuth2Request = OAuth2Request.builder()
                .token("google-token")
                .build();
    }

    @Test
    void register_success() throws Exception {
        // GIVEN
        when(userService.register(any())).thenReturn(userResponse);

        // WHEN, THEN
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userCreationRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.email").value("test@example.com"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.username").value("testuser"));
    }

    @Test
    void login_success() throws Exception {
        // GIVEN
        when(authService.login(any())).thenReturn(authResponse);

        // WHEN, THEN
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.accessToken").value("access-token"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.user.email").value("test@example.com"));
    }

    @Test
    void refreshToken_success() throws Exception {
        // GIVEN
        when(authService.refreshToken(any())).thenReturn(authResponse);

        // WHEN, THEN
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/auth/refresh-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshTokenRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.accessToken").value("access-token"));
    }

    @Test
    void loginWithGoogle_success() throws Exception {
        // GIVEN
        when(oAuth2Service.loginWithGoogle(any())).thenReturn(authResponse);

        // WHEN, THEN
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/auth/oauth2/google")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(oAuth2Request)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.accessToken").value("access-token"));
    }

    @Test
    void logout_success() throws Exception {
        // WHEN, THEN
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/auth/logout")
                        .header("Authorization", "Bearer access-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshTokenRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").value("Logout successfully"));
    }
}
