package com.example.cart_service.service;

import com.example.cart_service.dto.AddToCartRequest;
import com.example.cart_service.dto.CartResponse;
import com.example.cart_service.dto.UpdateCartItemRequest;

public interface CartService {
    
    /**
     * Get or create cart for user
     */
    CartResponse getOrCreateCartForUser(Long userId);
    
    /**
     * Get or create cart for guest session
     */
    CartResponse getOrCreateCartForGuest(String sessionId);
    
    /**
     * Add item to cart
     */
    CartResponse addToCart(Long userId, String sessionId, AddToCartRequest request);
    
    /**
     * Update cart item quantity
     */
    CartResponse updateCartItem(Long userId, String sessionId, Long cartItemId, UpdateCartItemRequest request);
    
    /**
     * Remove item from cart
     */
    CartResponse removeFromCart(Long userId, String sessionId, Long cartItemId);
    
    /**
     * Clear all items from cart
     */
    CartResponse clearCart(Long userId, String sessionId);
    
    /**
     * Get cart by user ID
     */
    CartResponse getCartByUserId(Long userId);
    
    /**
     * Get cart by session ID
     */
    CartResponse getCartBySessionId(String sessionId);
    
    /**
     * Transfer guest cart to user account
     */
    CartResponse transferGuestCartToUser(String sessionId, Long userId);
    
    /**
     * Validate cart items (check availability, prices)
     */
    CartResponse validateCart(Long userId, String sessionId);
    
    /**
     * Mark cart as converted to order
     */
    void markCartAsConverted(Long cartId);
    
    /**
     * Clean up expired carts
     */
    void cleanupExpiredCarts();
    
    /**
     * Extend cart expiration
     */
    CartResponse extendCartExpiration(Long userId, String sessionId, int days);
} 