package com.example.common.security;

import com.example.user.entity.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.security.Principal;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class StompUserPrincipal implements Principal {

    private final UUID userId;
    private final User user;

    @Override
    public String getName() {
        return userId.toString();
    }
}
