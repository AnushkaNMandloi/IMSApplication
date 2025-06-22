package com.example.cart_service.controller;

import com.example.cart_service.dto.AddToCartRequest;
import com.example.cart_service.dto.CartResponse;
import com.example.cart_service.dto.UpdateCartItemRequest;
import com.example.cart_service.service.CartService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/cart")
@CrossOrigin(origins = "*", maxAge = 3600)
public class CartController {
    
    private static final Logger logger = LoggerFactory.getLogger(CartController.class);
    
    @Autowired
    private CartService cartService;
    
    /**
     * Get current user's cart or create new one
     */
    @GetMapping
    public ResponseEntity<?> getCart(HttpServletRequest request) {
        try {
            Long userId = getCurrentUserId();
            String sessionId = getSessionId(request);
            
            CartResponse cart;
            if (userId != null) {
                cart = cartService.getCartByUserId(userId);
            } else {
                cart = cartService.getCartBySessionId(sessionId);
            }
            
            return ResponseEntity.ok(cart);
        } catch (Exception e) {
            logger.error("Error getting cart: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get cart", "message", e.getMessage()));
        }
    }
    
    /**
     * Add item to cart
     */
    @PostMapping("/items")
    public ResponseEntity<?> addToCart(@Valid @RequestBody AddToCartRequest request, 
                                      HttpServletRequest httpRequest) {
        try {
            Long userId = getCurrentUserId();
            String sessionId = getSessionId(httpRequest);
            
            CartResponse cart = cartService.addToCart(userId, sessionId, request);
            
            return ResponseEntity.ok(cart);
        } catch (Exception e) {
            logger.error("Error adding item to cart: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Failed to add item to cart", "message", e.getMessage()));
        }
    }
    
    /**
     * Update cart item quantity
     */
    @PutMapping("/items/{cartItemId}")
    public ResponseEntity<?> updateCartItem(@PathVariable Long cartItemId,
                                           @Valid @RequestBody UpdateCartItemRequest request,
                                           HttpServletRequest httpRequest) {
        try {
            Long userId = getCurrentUserId();
            String sessionId = getSessionId(httpRequest);
            
            CartResponse cart = cartService.updateCartItem(userId, sessionId, cartItemId, request);
            
            return ResponseEntity.ok(cart);
        } catch (Exception e) {
            logger.error("Error updating cart item: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Failed to update cart item", "message", e.getMessage()));
        }
    }
    
    /**
     * Remove item from cart
     */
    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<?> removeFromCart(@PathVariable Long cartItemId,
                                           HttpServletRequest httpRequest) {
        try {
            Long userId = getCurrentUserId();
            String sessionId = getSessionId(httpRequest);
            
            CartResponse cart = cartService.removeFromCart(userId, sessionId, cartItemId);
            
            return ResponseEntity.ok(cart);
        } catch (Exception e) {
            logger.error("Error removing item from cart: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Failed to remove item from cart", "message", e.getMessage()));
        }
    }
    
    /**
     * Clear all items from cart
     */
    @DeleteMapping
    public ResponseEntity<?> clearCart(HttpServletRequest httpRequest) {
        try {
            Long userId = getCurrentUserId();
            String sessionId = getSessionId(httpRequest);
            
            CartResponse cart = cartService.clearCart(userId, sessionId);
            
            return ResponseEntity.ok(cart);
        } catch (Exception e) {
            logger.error("Error clearing cart: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to clear cart", "message", e.getMessage()));
        }
    }
    
    /**
     * Transfer guest cart to user account (called after login)
     */
    @PostMapping("/transfer")
    @PreAuthorize("hasRole('USER') or hasRole('SELLER') or hasRole('ADMIN')")
    public ResponseEntity<?> transferGuestCart(@RequestParam String guestSessionId) {
        try {
            Long userId = getCurrentUserId();
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "User must be logged in to transfer cart"));
            }
            
            CartResponse cart = cartService.transferGuestCartToUser(guestSessionId, userId);
            
            return ResponseEntity.ok(cart);
        } catch (Exception e) {
            logger.error("Error transferring guest cart: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Failed to transfer guest cart", "message", e.getMessage()));
        }
    }
    
    /**
     * Validate cart items (check prices, availability)
     */
    @PostMapping("/validate")
    public ResponseEntity<?> validateCart(HttpServletRequest httpRequest) {
        try {
            Long userId = getCurrentUserId();
            String sessionId = getSessionId(httpRequest);
            
            CartResponse cart = cartService.validateCart(userId, sessionId);
            
            return ResponseEntity.ok(cart);
        } catch (Exception e) {
            logger.error("Error validating cart: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to validate cart", "message", e.getMessage()));
        }
    }
    
    /**
     * Extend cart expiration
     */
    @PostMapping("/extend")
    public ResponseEntity<?> extendCartExpiration(@RequestParam(defaultValue = "7") int days,
                                                 HttpServletRequest httpRequest) {
        try {
            Long userId = getCurrentUserId();
            String sessionId = getSessionId(httpRequest);
            
            CartResponse cart = cartService.extendCartExpiration(userId, sessionId, days);
            
            return ResponseEntity.ok(cart);
        } catch (Exception e) {
            logger.error("Error extending cart expiration: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Failed to extend cart expiration", "message", e.getMessage()));
        }
    }
    
    /**
     * Get cart summary (item count and total)
     */
    @GetMapping("/summary")
    public ResponseEntity<?> getCartSummary(HttpServletRequest httpRequest) {
        try {
            Long userId = getCurrentUserId();
            String sessionId = getSessionId(httpRequest);
            
            CartResponse cart;
            if (userId != null) {
                cart = cartService.getCartByUserId(userId);
            } else {
                cart = cartService.getCartBySessionId(sessionId);
            }
            
            Map<String, Object> summary = Map.of(
                    "totalItems", cart.getTotalItems(),
                    "totalAmount", cart.getTotalAmount(),
                    "itemCount", cart.getItems() != null ? cart.getItems().size() : 0,
                    "isEmpty", cart.getTotalItems() == 0
            );
            
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            logger.error("Error getting cart summary: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get cart summary", "message", e.getMessage()));
        }
    }
    
    /**
     * Admin endpoint to cleanup expired carts
     */
    @PostMapping("/admin/cleanup")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> cleanupExpiredCarts() {
        try {
            cartService.cleanupExpiredCarts();
            return ResponseEntity.ok(Map.of("message", "Expired carts cleanup completed"));
        } catch (Exception e) {
            logger.error("Error cleaning up expired carts: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to cleanup expired carts", "message", e.getMessage()));
        }
    }
    
    // Helper methods
    
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && 
            !authentication.getName().equals("anonymousUser")) {
            try {
                return Long.parseLong(authentication.getName());
            } catch (NumberFormatException e) {
                logger.debug("Authentication name is not a valid user ID: {}", authentication.getName());
            }
        }
        return null;
    }
    
    private String getSessionId(HttpServletRequest request) {
        // Try to get session ID from header first
        String sessionId = request.getHeader("X-Session-ID");
        if (sessionId == null || sessionId.trim().isEmpty()) {
            // Fall back to HTTP session
            sessionId = request.getSession().getId();
        }
        return sessionId;
    }
} 