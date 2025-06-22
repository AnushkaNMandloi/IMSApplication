package com.example.user_service.service;

import com.example.user_service.dto.*;
import com.example.user_service.model.UserProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface UserProfileService {

    // Profile management
    UserProfileResponse createProfile(Long userId, UserProfileRequest request);
    UserProfileResponse updateProfile(Long userId, UserProfileRequest request);
    UserProfileResponse getProfile(Long userId);
    UserProfileResponse getProfileByUsername(String username);
    void deleteProfile(Long userId);

    // Address management
    UserAddressResponse addAddress(Long userId, UserAddressRequest request);
    UserAddressResponse updateAddress(Long userId, Long addressId, UserAddressRequest request);
    void deleteAddress(Long userId, Long addressId);
    void setDefaultAddress(Long userId, Long addressId);
    List<UserAddressResponse> getUserAddresses(Long userId);
    UserAddressResponse getDefaultAddress(Long userId);

    // Phone verification
    void sendPhoneVerificationCode(Long userId);
    void verifyPhoneNumber(Long userId, String code);

    // Email verification
    void sendAlternateEmailVerification(Long userId);
    void verifyAlternateEmail(Long userId, String token);

    // Profile picture
    String uploadProfilePicture(Long userId, byte[] imageData, String fileName);
    void deleteProfilePicture(Long userId);

    // Analytics and stats
    void updateOrderStats(Long userId, BigDecimal orderAmount);
    void addLoyaltyPoints(Long userId, Integer points);
    void updateLoginStats(Long userId);

    // Search and discovery
    Page<UserProfileResponse> searchProfiles(String searchTerm, Pageable pageable);
    Page<UserProfileResponse> getPublicProfiles(Pageable pageable);
    List<UserProfileResponse> getHighValueCustomers(BigDecimal minSpent);
    List<UserProfileResponse> getActiveCustomers(Integer minOrders);

    // Admin functions
    List<UserProfileResponse> getUsersByMembershipTier(UserProfile.MembershipTier tier);
    BigDecimal getAverageCustomerSpending();
    Long getTotalCustomersByTier(UserProfile.MembershipTier tier);
} 