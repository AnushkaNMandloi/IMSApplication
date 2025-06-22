package com.example.user_service.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
@Slf4j
public class RateLimitingFilter extends OncePerRequestFilter {
    
    private final ConcurrentHashMap<String, Integer> requestCounts;
    private final ConcurrentHashMap<String, Long> requestTimestamps;
    
    private static final int MAX_REQUESTS_PER_MINUTE = 60; // General limit
    private static final int MAX_AUTH_REQUESTS_PER_MINUTE = 5; // Stricter limit for auth endpoints
    private static final long WINDOW_SIZE_MS = 60 * 1000; // 1 minute
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        
        String clientId = getClientIdentifier(request);
        String requestPath = request.getRequestURI();
        
        // Determine rate limit based on endpoint
        int maxRequests = isAuthEndpoint(requestPath) ? MAX_AUTH_REQUESTS_PER_MINUTE : MAX_REQUESTS_PER_MINUTE;
        
        if (isRateLimitExceeded(clientId, maxRequests)) {
            log.warn("Rate limit exceeded for client: {} on path: {}", clientId, requestPath);
            sendRateLimitExceededResponse(response);
            return;
        }
        
        filterChain.doFilter(request, response);
    }
    
    private String getClientIdentifier(HttpServletRequest request) {
        // Use X-Forwarded-For header if available (for load balancers/proxies)
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        // Fallback to remote address
        return request.getRemoteAddr();
    }
    
    private boolean isAuthEndpoint(String path) {
        return path.startsWith("/auth/login") || 
               path.startsWith("/auth/register") || 
               path.startsWith("/auth/forgot-password") ||
               path.startsWith("/auth/reset-password");
    }
    
    private boolean isRateLimitExceeded(String clientId, int maxRequests) {
        long currentTime = System.currentTimeMillis();
        
        // Clean up old entries for this client
        Long lastRequestTime = requestTimestamps.get(clientId);
        if (lastRequestTime != null && (currentTime - lastRequestTime) > WINDOW_SIZE_MS) {
            requestCounts.remove(clientId);
            requestTimestamps.remove(clientId);
        }
        
        // Update request count and timestamp
        int currentCount = requestCounts.getOrDefault(clientId, 0);
        if (currentCount == 0) {
            // First request in window
            requestTimestamps.put(clientId, currentTime);
        }
        
        requestCounts.put(clientId, currentCount + 1);
        
        return currentCount >= maxRequests;
    }
    
    private void sendRateLimitExceededResponse(HttpServletResponse response) throws IOException {
        response.setStatus(429); // 429 Too Many Requests
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "Rate limit exceeded");
        errorResponse.put("message", "Too many requests. Please try again later.");
        errorResponse.put("status", 429);
        errorResponse.put("timestamp", System.currentTimeMillis());
        
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getWriter(), errorResponse);
    }
    
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        
        // Skip rate limiting for health checks and actuator endpoints
        return path.startsWith("/actuator/") || 
               path.equals("/user/test") ||
               path.startsWith("/error");
    }
} 