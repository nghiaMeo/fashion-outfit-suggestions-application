package com.example.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
public class UserStatusResponse {
    private UUID userId;
    private boolean isOnline;
    private Instant lastSeen;
}
