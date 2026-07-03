package com.example.common.security;

import com.example.common.dto.ErrorResponse;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class RateLimitFilter extends OncePerRequestFilter {

    private final StringRedisTemplate redisTemplate;

    private static final Map<String, Integer> RATE_LIMITS = Map.of(
            "/auth/login", 10,
            "/auth/register", 5,
            "/auth/forgot-password", 3,
            "/auth/reset-password", 5,
            "/auth/refresh-token", 20,
            "/auth/oauth2/google", 10
    );

    private static final Duration RATE_LIMIT_WINDOW = Duration.ofSeconds(60);

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        var path = normalizePath(request);
        var maxRequests = RATE_LIMITS.get(path);

        if (maxRequests == null) {
            filterChain.doFilter(request, response);
            return;
        }

        var clientIp = getClientIp(request);
        var redisKey = "rate:" + clientIp + ":" + path;
        var currentCount = redisTemplate.opsForValue().increment(redisKey);

        if (currentCount != null) {
            if (currentCount == 1) {
                redisTemplate.expire(redisKey, RATE_LIMIT_WINDOW);
            }
            if (currentCount > maxRequests) {
                log.warn("Rate limit exceeded for IP: {} on endpoint: {} (count: {})", clientIp, path, currentCount);
                writeErrorResponse(response);
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    private String normalizePath(HttpServletRequest request) {
        var path = request.getRequestURI();
        var contextPath = request.getContextPath();
        if (contextPath != null && !contextPath.isEmpty() && path.startsWith(contextPath)) {
            path = path.substring(contextPath.length());
        }
        return path;
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private void writeErrorResponse(HttpServletResponse response) throws IOException {
        response.setStatus(429);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        var errorResponse = ErrorResponse.builder()
                .code(429)
                .message("Too many requests, please try again later")
                .build();
        var mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        response.getWriter().write(mapper.writeValueAsString(errorResponse));
    }
}
