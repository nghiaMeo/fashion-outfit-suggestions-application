package com.example.wardrobeservices.configuration;

import com.example.wardrobeservices.dto.response.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.io.IOException;
import java.time.Duration;
import java.util.Map;


@Component
@RequiredArgsConstructor
@Slf4j
public class RateLimitFilter extends OncePerRequestFilter {

    private final StringRedisTemplate redisTemplate;


    private static final Map<String, Integer> RATE_LIMITS = Map.of(
            "/api/auth/login", 10,
            "/api/auth/register", 5,
            "/api/auth/forgot-password", 3,
            "/api/auth/reset-password", 5
    );


    private static final Duration RATE_LIMIT_WINDOW = Duration.ofSeconds(60);

    /**
     * Enforces per-client, per-endpoint fixed-window rate limits and either forwards the request or responds with HTTP 429 when the limit is exceeded.
     *
     * <p>For request URIs configured in RATE_LIMITS, increments a per-client counter (keyed by client IP and path) in Redis with a RATE_LIMIT_WINDOW expiration (60 seconds). If the counter exceeds the configured maximum, writes a JSON HTTP 429 response and stops further filter-chain processing; otherwise forwards the request down the filter chain. The client IP is taken from the first value of the X-Forwarded-For header when present, or from request.getRemoteAddr() otherwise.</p>
     *
     * @param request     the incoming HTTP request
     * @param response    the HTTP response used to write a 429 error when the rate limit is exceeded
     * @param filterChain the filter chain to continue processing when the request is allowed
     */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        var path = request.getRequestURI();
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
                // request stop can not go to controller
            }
        }
        // < RATE_LIMIT go to JwtAuthenticationFilter >> SecurityConfig >> Controller
        filterChain.doFilter(request, response);
    }

    /**
     * Extracts the client's IP address from the request, preferring the first value in the `X-Forwarded-For` header when present.
     *
     * @param request the HTTP servlet request to inspect; may contain an `X-Forwarded-For` header with one or more comma-separated IPs
     * @return the client's IP address — the first IP from `X-Forwarded-For` if present and non-empty, otherwise the request's remote address
     */
    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");

        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            //get real ip if client has many ip first ip always real
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    /**
     * Writes a 429 (Too Many Requests) JSON error response to the given HttpServletResponse.
     *
     * The response's status is set to 429 and its Content-Type to "application/json". The body
     * is a serialized ErrorResponse with code 429 and message "Too many requests, please try again later".
     *
     * @param response the HttpServletResponse to write the error to
     * @throws IOException if an I/O error occurs while writing the response body
     */
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
