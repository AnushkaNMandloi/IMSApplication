package com.example.cart_service.repository;

import com.example.cart_service.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    
    /**
     * Find cart items by cart ID
     */
    List<CartItem> findByCartId(Long cartId);
    
    /**
     * Find cart item by cart ID and item ID
     */
    @Query("SELECT ci FROM CartItem ci WHERE ci.cart.id = :cartId AND ci.itemId = :itemId")
    Optional<CartItem> findByCartIdAndItemId(@Param("cartId") Long cartId, @Param("itemId") Long itemId);
    
    /**
     * Find cart item by cart ID, item ID and product attributes (for variants)
     */
    @Query("SELECT ci FROM CartItem ci WHERE ci.cart.id = :cartId AND ci.itemId = :itemId AND " +
           "(ci.productAttributes = :attributes OR (ci.productAttributes IS NULL AND :attributes IS NULL))")
    Optional<CartItem> findByCartIdAndItemIdAndAttributes(@Param("cartId") Long cartId, 
                                                         @Param("itemId") Long itemId,
                                                         @Param("attributes") String attributes);
    
    /**
     * Find all cart items by item ID (across all carts)
     */
    List<CartItem> findByItemId(Long itemId);
    
    /**
     * Find cart items by seller ID
     */
    List<CartItem> findBySellerId(Long sellerId);
    
    /**
     * Count items in a specific cart
     */
    @Query("SELECT COUNT(ci) FROM CartItem ci WHERE ci.cart.id = :cartId")
    long countByCartId(@Param("cartId") Long cartId);
    
    /**
     * Get total quantity of a specific item across all active carts
     */
    @Query("SELECT COALESCE(SUM(ci.quantity), 0) FROM CartItem ci JOIN ci.cart c " +
           "WHERE ci.itemId = :itemId AND c.status = 'ACTIVE'")
    long getTotalQuantityInActiveCarts(@Param("itemId") Long itemId);
    
    /**
     * Delete all cart items for a specific cart
     */
    void deleteByCartId(Long cartId);
    
    /**
     * Find cart items that need price updates (when item prices change)
     */
    @Query("SELECT ci FROM CartItem ci JOIN ci.cart c WHERE ci.itemId IN :itemIds AND c.status = 'ACTIVE'")
    List<CartItem> findActiveCartItemsByItemIds(@Param("itemIds") List<Long> itemIds);
} 