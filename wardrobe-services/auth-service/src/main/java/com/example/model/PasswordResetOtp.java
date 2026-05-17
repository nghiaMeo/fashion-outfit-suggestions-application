package com.example.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@RedisHash("PasswordResetOtp")
public class PasswordResetOtp {

    @Id
    private String id;

    @Indexed
    private String email;

    private String otp;

    @Builder.Default
    private Instant createdAt = Instant.now();

    @TimeToLive
    private Long expirySeconds; // 180 seconds = 3 minutes
}
