package com.example.user_service.dto;

import com.example.user_service.model.UserProfile;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileResponse {

    private Long id;
    private Long userId;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String fullName;
    private String profilePictureUrl;
    private LocalDate dateOfBirth;
    private UserProfile.Gender gender;
    private String phoneNumber;
    private Boolean phoneVerified;
    private String alternateEmail;
    private Boolean alternateEmailVerified;
    private String bio;
    private String websiteUrl;
    private String occupation;
    private String company;
    private String facebookUrl;
    private String twitterUrl;
    private String linkedinUrl;
    private String instagramUrl;
    private UserProfile.ProfileVisibility profileVisibility;
    private Boolean emailNotifications;
    private Boolean smsNotifications;
    private Boolean marketingEmails;
    private Integer totalOrders;
    private BigDecimal totalSpent;
    private Integer loyaltyPoints;
    private UserProfile.MembershipTier membershipTier;
    private LocalDateTime lastLoginAt;
    private Integer loginCount;
    private List<UserAddressResponse> addresses;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public UserProfileResponse(UserProfile profile) {
        this.id = profile.getId();
        this.userId = profile.getUser().getId();
        this.username = profile.getUser().getUsername();
        this.email = profile.getUser().getEmail();
        this.firstName = profile.getFirstName();
        this.lastName = profile.getLastName();
        this.fullName = profile.getFullName();
        this.profilePictureUrl = profile.getProfilePictureUrl();
        this.dateOfBirth = profile.getDateOfBirth();
        this.gender = profile.getGender();
        this.phoneNumber = profile.getPhoneNumber();
        this.phoneVerified = profile.getPhoneVerified();
        this.alternateEmail = profile.getAlternateEmail();
        this.alternateEmailVerified = profile.getAlternateEmailVerified();
        this.bio = profile.getBio();
        this.websiteUrl = profile.getWebsiteUrl();
        this.occupation = profile.getOccupation();
        this.company = profile.getCompany();
        this.facebookUrl = profile.getFacebookUrl();
        this.twitterUrl = profile.getTwitterUrl();
        this.linkedinUrl = profile.getLinkedinUrl();
        this.instagramUrl = profile.getInstagramUrl();
        this.profileVisibility = profile.getProfileVisibility();
        this.emailNotifications = profile.getEmailNotifications();
        this.smsNotifications = profile.getSmsNotifications();
        this.marketingEmails = profile.getMarketingEmails();
        this.totalOrders = profile.getTotalOrders();
        this.totalSpent = profile.getTotalSpent();
        this.loyaltyPoints = profile.getLoyaltyPoints();
        this.membershipTier = profile.getMembershipTier();
        this.lastLoginAt = profile.getLastLoginAt();
        this.loginCount = profile.getLoginCount();
        this.createdAt = profile.getCreatedAt();
        this.updatedAt = profile.getUpdatedAt();
    }
} 