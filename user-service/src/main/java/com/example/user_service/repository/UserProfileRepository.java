package com.example.user_service.repository;

import com.example.user_service.model.UserProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

    Optional<UserProfile> findByUserId(Long userId);

    Optional<UserProfile> findByUserUsername(String username);

    Optional<UserProfile> findByUserEmail(String email);

    Optional<UserProfile> findByPhoneNumber(String phoneNumber);

    Optional<UserProfile> findByAlternateEmail(String alternateEmail);

    List<UserProfile> findByMembershipTier(UserProfile.MembershipTier membershipTier);

    @Query("SELECT p FROM UserProfile p WHERE p.profileVisibility = 'PUBLIC'")
    Page<UserProfile> findPublicProfiles(Pageable pageable);

    @Query("SELECT p FROM UserProfile p WHERE p.totalOrders >= :minOrders")
    List<UserProfile> findActiveCustomers(@Param("minOrders") Integer minOrders);

    @Query("SELECT p FROM UserProfile p WHERE p.totalSpent >= :minSpent")
    List<UserProfile> findHighValueCustomers(@Param("minSpent") BigDecimal minSpent);

    @Query("SELECT p FROM UserProfile p WHERE p.lastLoginAt >= :since")
    List<UserProfile> findRecentlyActiveUsers(@Param("since") LocalDateTime since);

    @Query("SELECT p FROM UserProfile p WHERE p.emailNotifications = true")
    List<UserProfile> findUsersWithEmailNotificationsEnabled();

    @Query("SELECT p FROM UserProfile p WHERE p.smsNotifications = true AND p.phoneVerified = true")
    List<UserProfile> findUsersWithSmsNotificationsEnabled();

    @Query("SELECT p FROM UserProfile p WHERE p.marketingEmails = true")
    List<UserProfile> findUsersWithMarketingEmailsEnabled();

    @Query("SELECT p FROM UserProfile p WHERE p.phoneVerified = false AND p.phoneNumber IS NOT NULL")
    List<UserProfile> findUsersWithUnverifiedPhones();

    @Query("SELECT p FROM UserProfile p WHERE p.alternateEmailVerified = false AND p.alternateEmail IS NOT NULL")
    List<UserProfile> findUsersWithUnverifiedAlternateEmails();

    @Modifying
    @Query("UPDATE UserProfile p SET p.totalOrders = p.totalOrders + 1, p.totalSpent = p.totalSpent + :amount WHERE p.user.id = :userId")
    void updateOrderStats(@Param("userId") Long userId, @Param("amount") BigDecimal amount);

    @Modifying
    @Query("UPDATE UserProfile p SET p.loyaltyPoints = p.loyaltyPoints + :points WHERE p.user.id = :userId")
    void addLoyaltyPoints(@Param("userId") Long userId, @Param("points") Integer points);

    @Modifying
    @Query("UPDATE UserProfile p SET p.loginCount = p.loginCount + 1, p.lastLoginAt = :loginTime WHERE p.user.id = :userId")
    void updateLoginStats(@Param("userId") Long userId, @Param("loginTime") LocalDateTime loginTime);

    @Modifying
    @Query("UPDATE UserProfile p SET p.phoneVerified = true, p.phoneVerificationCode = null, p.phoneVerificationExpiresAt = null WHERE p.user.id = :userId")
    void markPhoneAsVerified(@Param("userId") Long userId);

    @Modifying
    @Query("UPDATE UserProfile p SET p.alternateEmailVerified = true WHERE p.user.id = :userId")
    void markAlternateEmailAsVerified(@Param("userId") Long userId);

    @Query("SELECT COUNT(p) FROM UserProfile p WHERE p.membershipTier = :tier")
    Long countByMembershipTier(@Param("tier") UserProfile.MembershipTier tier);

    @Query("SELECT AVG(p.totalSpent) FROM UserProfile p WHERE p.totalOrders > 0")
    BigDecimal getAverageCustomerSpending();

    @Query("SELECT p FROM UserProfile p WHERE p.firstName LIKE %:name% OR p.lastName LIKE %:name% OR p.user.username LIKE %:name%")
    Page<UserProfile> searchByName(@Param("name") String name, Pageable pageable);

    @Query("SELECT p FROM UserProfile p WHERE p.occupation LIKE %:occupation%")
    List<UserProfile> findByOccupation(@Param("occupation") String occupation);

    @Query("SELECT p FROM UserProfile p WHERE p.company LIKE %:company%")
    List<UserProfile> findByCompany(@Param("company") String company);
} 