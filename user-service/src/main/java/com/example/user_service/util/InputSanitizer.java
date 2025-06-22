package com.example.user_service.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
@Slf4j
public class InputSanitizer {
    
    // Patterns for detecting potentially malicious input
    private static final Pattern SQL_INJECTION_PATTERN = Pattern.compile(
        "(?i)(union|select|insert|update|delete|drop|create|alter|exec|execute|script|javascript|vbscript|onload|onerror|alert|eval|expression|behavior|binding|import|meta|link|embed|object|iframe|frame|form|input|textarea|button)",
        Pattern.CASE_INSENSITIVE
    );
    
    private static final Pattern XSS_PATTERN = Pattern.compile(
        "(?i)(<script|</script|<iframe|</iframe|<object|</object|<embed|</embed|<form|</form|javascript:|vbscript:|data:|on\\w+\\s*=)",
        Pattern.CASE_INSENSITIVE
    );
    
    private static final Pattern HTML_TAG_PATTERN = Pattern.compile(
        "<[^>]+>",
        Pattern.CASE_INSENSITIVE
    );
    
    // Characters that need to be escaped
    private static final String[][] ESCAPE_MAPPINGS = {
        {"&", "&amp;"},
        {"<", "&lt;"},
        {">", "&gt;"},
        {"\"", "&quot;"},
        {"'", "&#x27;"},
        {"/", "&#x2F;"},
        {"`", "&#x60;"},
        {"=", "&#x3D;"}
    };
    
    /**
     * Sanitize input by removing HTML tags and escaping special characters
     */
    public String sanitize(String input) {
        if (input == null || input.trim().isEmpty()) {
            return input;
        }
        
        String sanitized = input.trim();
        
        // Remove HTML tags
        sanitized = HTML_TAG_PATTERN.matcher(sanitized).replaceAll("");
        
        // Escape special characters
        for (String[] mapping : ESCAPE_MAPPINGS) {
            sanitized = sanitized.replace(mapping[0], mapping[1]);
        }
        
        return sanitized;
    }
    
    /**
     * Validate input for potential security threats
     */
    public boolean isInputSafe(String input) {
        if (input == null) {
            return true;
        }
        
        // Check for SQL injection patterns
        if (SQL_INJECTION_PATTERN.matcher(input).find()) {
            log.warn("Potential SQL injection detected in input: {}", input.substring(0, Math.min(input.length(), 50)));
            return false;
        }
        
        // Check for XSS patterns
        if (XSS_PATTERN.matcher(input).find()) {
            log.warn("Potential XSS attack detected in input: {}", input.substring(0, Math.min(input.length(), 50)));
            return false;
        }
        
        return true;
    }
    
    /**
     * Sanitize email input specifically
     */
    public String sanitizeEmail(String email) {
        if (email == null) {
            return null;
        }
        
        return email.trim().toLowerCase();
    }
    
    /**
     * Sanitize username input
     */
    public String sanitizeUsername(String username) {
        if (username == null) {
            return null;
        }
        
        // Remove any non-alphanumeric characters except underscore and hyphen
        String sanitized = username.trim().replaceAll("[^a-zA-Z0-9_-]", "");
        
        return sanitized;
    }
    
    /**
     * Validate and sanitize search input
     */
    public String sanitizeSearchInput(String searchTerm) {
        if (searchTerm == null) {
            return null;
        }
        
        if (!isInputSafe(searchTerm)) {
            throw new IllegalArgumentException("Invalid search term detected");
        }
        
        return sanitize(searchTerm);
    }
    
    /**
     * Check if input length is within acceptable limits
     */
    public boolean isLengthValid(String input, int maxLength) {
        return input == null || input.length() <= maxLength;
    }
    
    /**
     * Comprehensive input validation and sanitization
     */
    public String validateAndSanitize(String input, int maxLength) {
        if (input == null) {
            return null;
        }
        
        if (!isLengthValid(input, maxLength)) {
            throw new IllegalArgumentException("Input exceeds maximum length of " + maxLength + " characters");
        }
        
        if (!isInputSafe(input)) {
            throw new IllegalArgumentException("Input contains potentially malicious content");
        }
        
        return sanitize(input);
    }
} 