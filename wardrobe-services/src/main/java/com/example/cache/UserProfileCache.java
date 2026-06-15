package com.example.cache;

import com.example.dto.response.UserProfileResponse;
import com.example.service.UserService;
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

    private final UserService userService;
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
                var profiles = userService.getUsersProfiles(missingIds);
                if (profiles != null) {
                    for (UserProfileResponse profile : profiles) {
                        result.put(profile.getId(), profile);
                        if (cache != null) {
                            cache.put(profile.getId(), profile);
                        }
                    }
                }
            } catch (Exception e) {
                // Graceful fallback
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
            var profile = userService.getUserProfile(userId);
            if (profile != null) {
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
