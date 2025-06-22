package com.example.user_service.repository;

import com.example.user_service.model.UserAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserAddressRepository extends JpaRepository<UserAddress, Long> {

    List<UserAddress> findByUserProfileUserIdAndIsActiveTrue(Long userId);

    List<UserAddress> findByUserProfileUserId(Long userId);

    Optional<UserAddress> findByUserProfileUserIdAndIsDefaultTrue(Long userId);

    List<UserAddress> findByUserProfileUserIdAndAddressType(Long userId, UserAddress.AddressType addressType);

    Optional<UserAddress> findByIdAndUserProfileUserId(Long addressId, Long userId);

    List<UserAddress> findByCity(String city);

    List<UserAddress> findByState(String state);

    List<UserAddress> findByPostalCode(String postalCode);

    @Query("SELECT a FROM UserAddress a WHERE a.userProfile.user.id = :userId AND " +
           "(a.addressLine1 LIKE %:searchTerm% OR a.city LIKE %:searchTerm% OR a.state LIKE %:searchTerm%)")
    List<UserAddress> searchUserAddresses(@Param("userId") Long userId, @Param("searchTerm") String searchTerm);

    @Modifying
    @Query("UPDATE UserAddress a SET a.isDefault = false WHERE a.userProfile.user.id = :userId")
    void clearDefaultAddresses(@Param("userId") Long userId);

    @Modifying
    @Query("UPDATE UserAddress a SET a.isDefault = true WHERE a.id = :addressId AND a.userProfile.user.id = :userId")
    void setDefaultAddress(@Param("addressId") Long addressId, @Param("userId") Long userId);

    @Modifying
    @Query("UPDATE UserAddress a SET a.usageCount = a.usageCount + 1, a.lastUsedAt = CURRENT_TIMESTAMP WHERE a.id = :addressId")
    void incrementUsageCount(@Param("addressId") Long addressId);

    @Query("SELECT COUNT(a) FROM UserAddress a WHERE a.userProfile.user.id = :userId AND a.isActive = true")
    Long countActiveAddressesByUserId(@Param("userId") Long userId);

    @Query("SELECT a FROM UserAddress a WHERE a.userProfile.user.id = :userId ORDER BY a.usageCount DESC")
    List<UserAddress> findMostUsedAddresses(@Param("userId") Long userId);

    @Query("SELECT a FROM UserAddress a WHERE a.userProfile.user.id = :userId AND a.isActive = true ORDER BY a.lastUsedAt DESC")
    List<UserAddress> findRecentlyUsedAddresses(@Param("userId") Long userId);

    @Modifying
    @Query("UPDATE UserAddress a SET a.isActive = false WHERE a.id = :addressId AND a.userProfile.user.id = :userId")
    void deactivateAddress(@Param("addressId") Long addressId, @Param("userId") Long userId);

    @Modifying
    @Query("UPDATE UserAddress a SET a.isActive = true WHERE a.id = :addressId AND a.userProfile.user.id = :userId")
    void activateAddress(@Param("addressId") Long addressId, @Param("userId") Long userId);
} 