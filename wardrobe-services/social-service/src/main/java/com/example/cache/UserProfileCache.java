package com.example.cache;

import com.example.client.UserClient;
import com.example.dto.response.UserProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserProfileCache {

    private final UserClient userClient;
    private final CacheManager cacheManager;

    public Map<UUID, UserProfileResponse> getProfilesBatch(List<UUID> userIds) {
        Map<UUID, UserProfileResponse> result = new HashMap<>();
        List<UUID> missingIds = new ArrayList<>();

        var cache = cacheManager.getCache("userProfiles");

        for (UUID id : userIds) {
            if (cache != null) {
                var cachedValue = cache.get(id, UserProfileResponse.class);
                if (cachedValue != null) {
                    result.put(id, cachedValue);
                    continue;
                }
            }
            missingIds.add(id);
        }

        if (!missingIds.isEmpty()) {
            try {
                var response = userClient.getUsersProfiles(missingIds);
                if (response != null && response.getResult() != null) {
                    for (UserProfileResponse profile : response.getResult()) {
                        result.put(profile.getId(), profile);
                        if (cache != null) {
                            cache.put(profile.getId(), profile);
                        }
                    }
                }
            } catch (Exception e) {
                // Graceful fallback: auth-service is down. Return what we could find from cache
            }
        }

        return result;
    }

    public UserProfileResponse getProfile(UUID userId) {
        var cache = cacheManager.getCache("userProfiles");
        if (cache != null) {
            var cachedValue = cache.get(userId, UserProfileResponse.class);
            if (cachedValue != null) {
                return cachedValue;
            }
        }

        try {
            var response = userClient.getProfile(userId);
            if (response != null && response.getResult() != null) {
                var profile = response.getResult();
                if (cache != null) {
                    cache.put(userId, profile);
                }
                return profile;
            }
        } catch (Exception e) {
            // Graceful fallback
        }
        return null;
    }
}
