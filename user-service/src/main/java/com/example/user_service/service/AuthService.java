package com.example.user_service.service;

import com.example.user_service.dto.*;
import com.example.user_service.model.User;
import com.example.user_service.repository.UserRepository;
import com.example.user_service.security.CustomUserDetails;
import com.example.user_service.util.JwtUtil;
import com.example.user_service.util.InputSanitizer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final InputSanitizer inputSanitizer;
    
    @Transactional
    public AuthResponse login(LoginRequest loginRequest) {
        // Sanitize input
        String email = inputSanitizer.sanitizeEmail(loginRequest.getEmail());
        log.info("Login attempt for email: {}", email);
        
        User user = userRepository.findActiveByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + loginRequest.getEmail()));
        
        // Check if account is locked
        if (!user.isAccountNonLocked()) {
            log.warn("Login attempt for locked account: {}", loginRequest.getEmail());
            throw new BadCredentialsException("Account is locked due to multiple failed login attempts");
        }
        
        // Verify password
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            log.warn("Invalid password for user: {}", loginRequest.getEmail());
            user.incrementFailedAttempts();
            userRepository.save(user);
            throw new BadCredentialsException("Invalid credentials");
        }
        
        // Reset failed attempts on successful login
        if (user.getFailedLoginAttempts() > 0) {
            user.resetFailedAttempts();
        }
        
        // Generate tokens
        CustomUserDetails userDetails = new CustomUserDetails(user);
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("userId", user.getUserId());
        extraClaims.put("role", user.getRole());
        extraClaims.put("email", user.getEmail());
        
        String accessToken = jwtUtil.generateToken(userDetails, extraClaims);
        String refreshToken = jwtUtil.generateRefreshToken(userDetails);
        
        // Save refresh token
        user.setRefreshToken(refreshToken);
        user.setUpdatedBy("SYSTEM");
        userRepository.save(user);
        
        log.info("Successful login for user: {}", loginRequest.getEmail());
        
        AuthResponse.UserInfo userInfo = new AuthResponse.UserInfo(
                user.getUserId(),
                user.getUserName(),
                user.getEmail(),
                user.getRole(),
                user.getEmailVerified()
        );
        
        return new AuthResponse(accessToken, refreshToken, 86400000L, userInfo); // 24 hours
    }
    
    @Transactional
    public AuthResponse register(RegisterRequest registerRequest) {
        // Sanitize inputs
        String email = inputSanitizer.sanitizeEmail(registerRequest.getEmail());
        String userName = inputSanitizer.sanitizeUsername(registerRequest.getUserName());
        
        log.info("Registration attempt for email: {}", email);
        
        // Validate passwords match
        if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }
        
        // Validate input safety
        if (!inputSanitizer.isInputSafe(userName) || !inputSanitizer.isInputSafe(email)) {
            throw new IllegalArgumentException("Invalid input detected");
        }
        
        // Check if user already exists
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("User already exists with email: " + email);
        }
        
        if (userRepository.existsByUserName(userName)) {
            throw new IllegalArgumentException("Username already exists: " + userName);
        }
        
        // Create new user
        User user = new User();
        user.setUserName(userName);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRole(registerRequest.getRole());
        user.setPhoneNumber(registerRequest.getPhoneNumber());
        user.setEmailVerified(false);
        user.setCreatedBy("SYSTEM");
        user.setUpdatedBy("SYSTEM");
        
        User savedUser = userRepository.save(user);
        
        // Generate tokens for immediate login
        CustomUserDetails userDetails = new CustomUserDetails(savedUser);
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("userId", savedUser.getUserId());
        extraClaims.put("role", savedUser.getRole());
        extraClaims.put("email", savedUser.getEmail());
        
        String accessToken = jwtUtil.generateToken(userDetails, extraClaims);
        String refreshToken = jwtUtil.generateRefreshToken(userDetails);
        
        // Save refresh token
        savedUser.setRefreshToken(refreshToken);
        userRepository.save(savedUser);
        
        log.info("Successful registration for user: {}", registerRequest.getEmail());
        
        AuthResponse.UserInfo userInfo = new AuthResponse.UserInfo(
                savedUser.getUserId(),
                savedUser.getUserName(),
                savedUser.getEmail(),
                savedUser.getRole(),
                savedUser.getEmailVerified()
        );
        
        return new AuthResponse(accessToken, refreshToken, 86400000L, userInfo);
    }
    
    @Transactional
    public AuthResponse refreshToken(String refreshToken) {
        log.info("Token refresh attempt");
        
        if (!jwtUtil.validateToken(refreshToken) || !jwtUtil.isRefreshToken(refreshToken)) {
            throw new BadCredentialsException("Invalid refresh token");
        }
        
        String email = jwtUtil.extractUsername(refreshToken);
        User user = userRepository.findActiveByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        
        if (!refreshToken.equals(user.getRefreshToken())) {
            throw new BadCredentialsException("Refresh token does not match");
        }
        
        // Generate new tokens
        CustomUserDetails userDetails = new CustomUserDetails(user);
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("userId", user.getUserId());
        extraClaims.put("role", user.getRole());
        extraClaims.put("email", user.getEmail());
        
        String newAccessToken = jwtUtil.generateToken(userDetails, extraClaims);
        String newRefreshToken = jwtUtil.generateRefreshToken(userDetails);
        
        // Save new refresh token
        user.setRefreshToken(newRefreshToken);
        user.setUpdatedBy("SYSTEM");
        userRepository.save(user);
        
        log.info("Successful token refresh for user: {}", email);
        
        AuthResponse.UserInfo userInfo = new AuthResponse.UserInfo(
                user.getUserId(),
                user.getUserName(),
                user.getEmail(),
                user.getRole(),
                user.getEmailVerified()
        );
        
        return new AuthResponse(newAccessToken, newRefreshToken, 86400000L, userInfo);
    }
    
    @Transactional
    public void logout(String refreshToken) {
        log.info("Logout attempt");
        
        if (refreshToken != null && jwtUtil.validateToken(refreshToken)) {
            String email = jwtUtil.extractUsername(refreshToken);
            userRepository.findActiveByEmail(email).ifPresent(user -> {
                user.setRefreshToken(null);
                user.setUpdatedBy("SYSTEM");
                userRepository.save(user);
                log.info("Successful logout for user: {}", email);
            });
        }
    }
    
    public boolean validateAccessToken(String token) {
        return jwtUtil.validateToken(token) && !jwtUtil.isRefreshToken(token);
    }
    
    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        log.info("Password reset request for email: {}", request.getEmail());
        
        User user = userRepository.findActiveByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + request.getEmail()));
        
        // Generate reset token
        String resetToken = UUID.randomUUID().toString();
        user.setPasswordResetToken(resetToken);
        user.setPasswordResetTokenExpiry(LocalDateTime.now().plusHours(1)); // Token valid for 1 hour
        user.setUpdatedBy("SYSTEM");
        
        userRepository.save(user);
        
        // In a real application, you would send an email here
        log.info("Password reset token generated for user: {} (Token: {})", request.getEmail(), resetToken);
        log.info("In production, this token would be sent via email to the user");
    }
    
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        log.info("Password reset attempt with token");
        
        // Validate passwords match
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }
        
        // Find user by valid reset token
        User user = userRepository.findByValidPasswordResetToken(request.getToken(), LocalDateTime.now())
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired reset token"));
        
        // Update password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setPasswordResetToken(null);
        user.setPasswordResetTokenExpiry(null);
        user.setUpdatedBy("SYSTEM");
        
        // Clear any existing refresh tokens for security
        user.setRefreshToken(null);
        
        // Reset failed login attempts
        user.resetFailedAttempts();
        
        userRepository.save(user);
        
        log.info("Password successfully reset for user: {}", user.getEmail());
    }
} 