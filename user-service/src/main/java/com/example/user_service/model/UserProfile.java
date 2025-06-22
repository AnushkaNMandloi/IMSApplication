package com.example.user_service.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "user_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "first_name", length = 50)
    private String firstName;

    @Column(name = "last_name", length = 50)
    private String lastName;

    @Column(name = "profile_picture_url")
    private String profilePictureUrl;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

    @Column(name = "phone_number", length = 20)
    @Pattern(regexp = "^[+]?[0-9\\-\\s()]{7,20}$", message = "Invalid phone number format")
    private String phoneNumber;

    @Column(name = "phone_verified", nullable = false)
    private Boolean phoneVerified = false;

    @Column(name = "phone_verification_code", length = 6)
    private String phoneVerificationCode;

    @Column(name = "phone_verification_expires_at")
    private LocalDateTime phoneVerificationExpiresAt;

    @Column(name = "alternate_email")
    @Email(message = "Invalid alternate email format")
    private String alternateEmail;

    @Column(name = "alternate_email_verified", nullable = false)
    private Boolean alternateEmailVerified = false;

    @Column(name = "bio", length = 500)
    private String bio;

    @Column(name = "website_url")
    private String websiteUrl;

    @Column(name = "occupation", length = 100)
    private String occupation;

    @Column(name = "company", length = 100)
    private String company;

    @OneToMany(mappedBy = "userProfile", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<UserAddress> addresses;

    @OneToMany(mappedBy = "userProfile", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<UserPreference> preferences;

    // Social media links
    @Column(name = "facebook_url")
    private String facebookUrl;

    @Column(name = "twitter_url")
    private String twitterUrl;

    @Column(name = "linkedin_url")
    private String linkedinUrl;

    @Column(name = "instagram_url")
    private String instagramUrl;

    // Privacy settings
    @Column(name = "profile_visibility", nullable = false)
    @Enumerated(EnumType.STRING)
    private ProfileVisibility profileVisibility = ProfileVisibility.PUBLIC;

    @Column(name = "email_notifications", nullable = false)
    private Boolean emailNotifications = true;

    @Column(name = "sms_notifications", nullable = false)
    private Boolean smsNotifications = false;

    @Column(name = "marketing_emails", nullable = false)
    private Boolean marketingEmails = true;

    // Account metrics
    @Column(name = "total_orders", nullable = false)
    private Integer totalOrders = 0;

    @Column(name = "total_spent", precision = 10, scale = 2)
    private java.math.BigDecimal totalSpent = java.math.BigDecimal.ZERO;

    @Column(name = "loyalty_points", nullable = false)
    private Integer loyaltyPoints = 0;

    @Column(name = "membership_tier")
    @Enumerated(EnumType.STRING)
    private MembershipTier membershipTier = MembershipTier.BRONZE;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @Column(name = "login_count", nullable = false)
    private Integer loginCount = 0;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum Gender {
        MALE, FEMALE, OTHER, PREFER_NOT_TO_SAY
    }

    public enum ProfileVisibility {
        PUBLIC, FRIENDS_ONLY, PRIVATE
    }

    public enum MembershipTier {
        BRONZE, SILVER, GOLD, PLATINUM, DIAMOND
    }

    // Helper methods
    public String getFullName() {
        if (firstName == null && lastName == null) {
            return null;
        }
        return (firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "");
    }

    public boolean isPhoneVerificationExpired() {
        return phoneVerificationExpiresAt != null && phoneVerificationExpiresAt.isBefore(LocalDateTime.now());
    }

    public void incrementLoginCount() {
        this.loginCount = (this.loginCount != null ? this.loginCount : 0) + 1;
        this.lastLoginAt = LocalDateTime.now();
    }

    public void addLoyaltyPoints(Integer points) {
        this.loyaltyPoints = (this.loyaltyPoints != null ? this.loyaltyPoints : 0) + points;
        updateMembershipTier();
    }

    public void updateOrderStats(java.math.BigDecimal orderAmount) {
        this.totalOrders = (this.totalOrders != null ? this.totalOrders : 0) + 1;
        this.totalSpent = (this.totalSpent != null ? this.totalSpent : java.math.BigDecimal.ZERO).add(orderAmount);
        updateMembershipTier();
    }

    private void updateMembershipTier() {
        if (totalSpent != null) {
            if (totalSpent.compareTo(new java.math.BigDecimal("10000")) >= 0) {
                this.membershipTier = MembershipTier.DIAMOND;
            } else if (totalSpent.compareTo(new java.math.BigDecimal("5000")) >= 0) {
                this.membershipTier = MembershipTier.PLATINUM;
            } else if (totalSpent.compareTo(new java.math.BigDecimal("2000")) >= 0) {
                this.membershipTier = MembershipTier.GOLD;
            } else if (totalSpent.compareTo(new java.math.BigDecimal("500")) >= 0) {
                this.membershipTier = MembershipTier.SILVER;
            } else {
                this.membershipTier = MembershipTier.BRONZE;
            }
        }
    }
} 