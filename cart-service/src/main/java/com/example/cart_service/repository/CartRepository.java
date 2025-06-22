package com.example.cart_service.repository;

import com.example.cart_service.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    
    /**
     * Find active cart by user ID
     */
    @Query("SELECT c FROM Cart c WHERE c.userId = :userId AND c.status = 'ACTIVE'")
    Optional<Cart> findActiveCartByUserId(@Param("userId") Long userId);
    
    /**
     * Find active cart by session ID (for guest users)
     */
    @Query("SELECT c FROM Cart c WHERE c.sessionId = :sessionId AND c.status = 'ACTIVE'")
    Optional<Cart> findActiveCartBySessionId(@Param("sessionId") String sessionId);
    
    /**
     * Find all carts by user ID
     */
    List<Cart> findByUserId(Long userId);
    
    /**
     * Find all carts by session ID
     */
    List<Cart> findBySessionId(String sessionId);
    
    /**
     * Find expired carts
     */
    @Query("SELECT c FROM Cart c WHERE c.expiresAt < :currentTime AND c.status = 'ACTIVE'")
    List<Cart> findExpiredCarts(@Param("currentTime") LocalDateTime currentTime);
    
    /**
     * Update cart status to expired for expired carts
     */
    @Modifying
    @Query("UPDATE Cart c SET c.status = 'EXPIRED' WHERE c.expiresAt < :currentTime AND c.status = 'ACTIVE'")
    int markExpiredCarts(@Param("currentTime") LocalDateTime currentTime);
    
    /**
     * Delete expired carts older than specified days
     */
    @Modifying
    @Query("DELETE FROM Cart c WHERE c.status = 'EXPIRED' AND c.updatedAt < :cutoffDate")
    int deleteOldExpiredCarts(@Param("cutoffDate") LocalDateTime cutoffDate);
    
    /**
     * Find carts by status
     */
    List<Cart> findByStatus(Cart.CartStatus status);
    
    /**
     * Count active carts by user
     */
    @Query("SELECT COUNT(c) FROM Cart c WHERE c.userId = :userId AND c.status = 'ACTIVE'")
    long countActiveCartsByUserId(@Param("userId") Long userId);
    
    /**
     * Find carts that need cleanup (expired and old)
     */
    @Query("SELECT c FROM Cart c WHERE (c.status = 'EXPIRED' AND c.updatedAt < :oldDate) OR " +
           "(c.status = 'ABANDONED' AND c.updatedAt < :abandonedDate)")
    List<Cart> findCartsForCleanup(@Param("oldDate") LocalDateTime oldDate, 
                                  @Param("abandonedDate") LocalDateTime abandonedDate);
    
    /**
     * Transfer guest cart to user account
     */
    @Modifying
    @Query("UPDATE Cart c SET c.userId = :userId, c.sessionId = null WHERE c.sessionId = :sessionId AND c.status = 'ACTIVE'")
    int transferGuestCartToUser(@Param("sessionId") String sessionId, @Param("userId") Long userId);
} 