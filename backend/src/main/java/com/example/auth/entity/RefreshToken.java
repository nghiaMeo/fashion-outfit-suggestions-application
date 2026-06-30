package com.example.auth.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@RedisHash("RefreshToken")
public class RefreshToken {

    @Id
    private String id;

    @Indexed
    private String token;

    @Indexed
    private UUID userId;

    private String email;

    @TimeToLive
    private Long expirySeconds;

    @Builder.Default
    private Instant createdAt = Instant.now();
}
