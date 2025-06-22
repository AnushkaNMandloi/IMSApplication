package com.example.cart_service.service.impl;

import com.example.cart_service.dto.AddToCartRequest;
import com.example.cart_service.dto.CartResponse;
import com.example.cart_service.dto.UpdateCartItemRequest;
import com.example.cart_service.feign.ItemServiceClient;
import com.example.cart_service.model.Cart;
import com.example.cart_service.model.CartItem;
import com.example.cart_service.repository.CartRepository;
import com.example.cart_service.repository.CartItemRepository;
import com.example.cart_service.service.CartService;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class CartServiceImpl implements CartService {
    
    private static final Logger logger = LoggerFactory.getLogger(CartServiceImpl.class);
    
    @Autowired
    private CartRepository cartRepository;
    
    @Autowired
    private CartItemRepository cartItemRepository;
    
    @Autowired
    private ItemServiceClient itemServiceClient;
    
    @Value("${cart.expiration.days:7}")
    private int cartExpirationDays;
    
    @Value("${cart.max.items.per.cart:50}")
    private int maxItemsPerCart;
    
    @Override
    public CartResponse getOrCreateCartForUser(Long userId) {
        logger.info("Getting or creating cart for user: {}", userId);
        
        Optional<Cart> existingCart = cartRepository.findActiveCartByUserId(userId);
        
        if (existingCart.isPresent()) {
            Cart cart = existingCart.get();
            if (!cart.isExpired()) {
                cart.extendExpiration(cartExpirationDays);
                cart = cartRepository.save(cart);
                return new CartResponse(cart);
            } else {
                // Mark as expired and create new cart
                cart.setStatus(Cart.CartStatus.EXPIRED);
                cartRepository.save(cart);
            }
        }
        
        // Create new cart
        Cart newCart = new Cart(userId);
        newCart = cartRepository.save(newCart);
        logger.info("Created new cart with ID: {} for user: {}", newCart.getId(), userId);
        
        return new CartResponse(newCart);
    }
    
    @Override
    public CartResponse getOrCreateCartForGuest(String sessionId) {
        logger.info("Getting or creating cart for guest session: {}", sessionId);
        
        Optional<Cart> existingCart = cartRepository.findActiveCartBySessionId(sessionId);
        
        if (existingCart.isPresent()) {
            Cart cart = existingCart.get();
            if (!cart.isExpired()) {
                cart.extendExpiration(cartExpirationDays);
                cart = cartRepository.save(cart);
                return new CartResponse(cart);
            } else {
                // Mark as expired and create new cart
                cart.setStatus(Cart.CartStatus.EXPIRED);
                cartRepository.save(cart);
            }
        }
        
        // Create new guest cart
        Cart newCart = new Cart(sessionId);
        newCart = cartRepository.save(newCart);
        logger.info("Created new guest cart with ID: {} for session: {}", newCart.getId(), sessionId);
        
        return new CartResponse(newCart);
    }
    
    @Override
    public CartResponse addToCart(Long userId, String sessionId, AddToCartRequest request) {
        logger.info("Adding item {} to cart for user: {} or session: {}", request.getItemId(), userId, sessionId);
        
        // Get or create cart
        Cart cart = getActiveCart(userId, sessionId);
        
        // Validate item exists and is available
        ItemServiceClient.ItemDto item = validateAndGetItem(request.getItemId());
        
        // Check cart item limit
        if (cart.getItems().size() >= maxItemsPerCart) {
            throw new RuntimeException("Cart has reached maximum item limit of " + maxItemsPerCart);
        }
        
        // Check if item already exists in cart (with same attributes)
        Optional<CartItem> existingItem = cartItemRepository.findByCartIdAndItemIdAndAttributes(
                cart.getId(), request.getItemId(), request.getProductAttributes());
        
        if (existingItem.isPresent()) {
            // Update quantity of existing item
            CartItem cartItem = existingItem.get();
            int newQuantity = cartItem.getQuantity() + request.getQuantity();
            
            // Validate stock availability
            validateStockAvailability(request.getItemId(), newQuantity);
            
            cartItem.setQuantity(newQuantity);
            cartItem.calculateSubtotal();
            cartItemRepository.save(cartItem);
            logger.info("Updated existing cart item quantity to: {}", newQuantity);
        } else {
            // Validate stock availability
            validateStockAvailability(request.getItemId(), request.getQuantity());
            
            // Create new cart item
            CartItem cartItem = new CartItem(item.getId(), item.getName(), item.getPrice(), request.getQuantity());
            cartItem.setItemDescription(item.getDescription());
            cartItem.setItemImageUrl(item.getImageUrl());
            cartItem.setSellerId(item.getSellerId());
            cartItem.setSellerName(item.getSellerName());
            cartItem.setProductAttributes(request.getProductAttributes());
            
            cart.addItem(cartItem);
            cartItemRepository.save(cartItem);
            logger.info("Added new item to cart: {}", cartItem.getId());
        }
        
        cart.updateTotals();
        cart = cartRepository.save(cart);
        
        return new CartResponse(cart);
    }
    
    @Override
    public CartResponse updateCartItem(Long userId, String sessionId, Long cartItemId, UpdateCartItemRequest request) {
        logger.info("Updating cart item {} quantity to: {}", cartItemId, request.getQuantity());
        
        Cart cart = getActiveCart(userId, sessionId);
        
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found: " + cartItemId));
        
        // Verify cart item belongs to the user's cart
        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new RuntimeException("Cart item does not belong to user's cart");
        }
        
        // Validate stock availability
        validateStockAvailability(cartItem.getItemId(), request.getQuantity());
        
        cartItem.updateQuantity(request.getQuantity());
        cartItemRepository.save(cartItem);
        
        cart.updateTotals();
        cart = cartRepository.save(cart);
        
        return new CartResponse(cart);
    }
    
    @Override
    public CartResponse removeFromCart(Long userId, String sessionId, Long cartItemId) {
        logger.info("Removing cart item: {}", cartItemId);
        
        Cart cart = getActiveCart(userId, sessionId);
        
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found: " + cartItemId));
        
        // Verify cart item belongs to the user's cart
        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new RuntimeException("Cart item does not belong to user's cart");
        }
        
        cart.removeItem(cartItem);
        cartItemRepository.delete(cartItem);
        
        cart.updateTotals();
        cart = cartRepository.save(cart);
        
        return new CartResponse(cart);
    }
    
    @Override
    public CartResponse clearCart(Long userId, String sessionId) {
        logger.info("Clearing cart for user: {} or session: {}", userId, sessionId);
        
        Cart cart = getActiveCart(userId, sessionId);
        
        cart.clearItems();
        cartItemRepository.deleteByCartId(cart.getId());
        
        cart.updateTotals();
        cart = cartRepository.save(cart);
        
        return new CartResponse(cart);
    }
    
    @Override
    public CartResponse getCartByUserId(Long userId) {
        Optional<Cart> cart = cartRepository.findActiveCartByUserId(userId);
        if (cart.isPresent() && !cart.get().isExpired()) {
            return new CartResponse(cart.get());
        }
        return getOrCreateCartForUser(userId);
    }
    
    @Override
    public CartResponse getCartBySessionId(String sessionId) {
        Optional<Cart> cart = cartRepository.findActiveCartBySessionId(sessionId);
        if (cart.isPresent() && !cart.get().isExpired()) {
            return new CartResponse(cart.get());
        }
        return getOrCreateCartForGuest(sessionId);
    }
    
    @Override
    public CartResponse transferGuestCartToUser(String sessionId, Long userId) {
        logger.info("Transferring guest cart from session: {} to user: {}", sessionId, userId);
        
        Optional<Cart> guestCart = cartRepository.findActiveCartBySessionId(sessionId);
        if (!guestCart.isPresent()) {
            return getOrCreateCartForUser(userId);
        }
        
        Optional<Cart> userCart = cartRepository.findActiveCartByUserId(userId);
        
        if (userCart.isPresent()) {
            // Merge guest cart items into user cart
            Cart existingUserCart = userCart.get();
            Cart guestCartEntity = guestCart.get();
            
            for (CartItem guestItem : guestCartEntity.getItems()) {
                Optional<CartItem> existingUserItem = cartItemRepository.findByCartIdAndItemIdAndAttributes(
                        existingUserCart.getId(), guestItem.getItemId(), guestItem.getProductAttributes());
                
                if (existingUserItem.isPresent()) {
                    // Merge quantities
                    CartItem userItem = existingUserItem.get();
                    userItem.setQuantity(userItem.getQuantity() + guestItem.getQuantity());
                    userItem.calculateSubtotal();
                    cartItemRepository.save(userItem);
                } else {
                    // Transfer item to user cart
                    guestItem.setCart(existingUserCart);
                    cartItemRepository.save(guestItem);
                }
            }
            
            // Delete guest cart
            cartRepository.delete(guestCartEntity);
            
            existingUserCart.updateTotals();
            existingUserCart = cartRepository.save(existingUserCart);
            
            return new CartResponse(existingUserCart);
        } else {
            // Transfer guest cart to user
            int transferred = cartRepository.transferGuestCartToUser(sessionId, userId);
            if (transferred > 0) {
                Optional<Cart> transferredCart = cartRepository.findActiveCartByUserId(userId);
                return new CartResponse(transferredCart.get());
            }
        }
        
        return getOrCreateCartForUser(userId);
    }
    
    @Override
    public CartResponse validateCart(Long userId, String sessionId) {
        logger.info("Validating cart for user: {} or session: {}", userId, sessionId);
        
        Cart cart = getActiveCart(userId, sessionId);
        boolean cartUpdated = false;
        
        for (CartItem item : cart.getItems()) {
            try {
                // Check if item still exists and get current price
                ItemServiceClient.ItemDto currentItem = itemServiceClient.getItemById(item.getItemId()).getBody();
                
                if (currentItem == null) {
                    // Item no longer exists, remove from cart
                    cart.removeItem(item);
                    cartItemRepository.delete(item);
                    cartUpdated = true;
                    logger.warn("Removed non-existent item {} from cart", item.getItemId());
                    continue;
                }
                
                // Update price if changed
                if (!item.getPrice().equals(currentItem.getPrice())) {
                    item.setPrice(currentItem.getPrice());
                    item.calculateSubtotal();
                    cartItemRepository.save(item);
                    cartUpdated = true;
                    logger.info("Updated price for item {} in cart", item.getItemId());
                }
                
                // Check stock availability
                ItemServiceClient.ItemAvailabilityDto availability = 
                        itemServiceClient.checkItemAvailability(item.getItemId()).getBody();
                
                if (availability != null && !availability.isAvailable()) {
                    // Item no longer available, remove from cart
                    cart.removeItem(item);
                    cartItemRepository.delete(item);
                    cartUpdated = true;
                    logger.warn("Removed unavailable item {} from cart", item.getItemId());
                } else if (availability != null && item.getQuantity() > availability.getAvailableQuantity()) {
                    // Reduce quantity to available amount
                    item.setQuantity(availability.getAvailableQuantity());
                    item.calculateSubtotal();
                    cartItemRepository.save(item);
                    cartUpdated = true;
                    logger.info("Reduced quantity for item {} to available stock", item.getItemId());
                }
                
            } catch (FeignException e) {
                logger.error("Error validating item {}: {}", item.getItemId(), e.getMessage());
                // Optionally remove item if service is unavailable
            }
        }
        
        if (cartUpdated) {
            cart.updateTotals();
            cart = cartRepository.save(cart);
        }
        
        return new CartResponse(cart);
    }
    
    @Override
    public void markCartAsConverted(Long cartId) {
        logger.info("Marking cart {} as converted to order", cartId);
        
        Optional<Cart> cart = cartRepository.findById(cartId);
        if (cart.isPresent()) {
            cart.get().setStatus(Cart.CartStatus.CONVERTED_TO_ORDER);
            cartRepository.save(cart.get());
        }
    }
    
    @Override
    public void cleanupExpiredCarts() {
        logger.info("Starting cleanup of expired carts");
        
        LocalDateTime now = LocalDateTime.now();
        
        // Mark expired carts
        int markedExpired = cartRepository.markExpiredCarts(now);
        logger.info("Marked {} carts as expired", markedExpired);
        
        // Delete old expired carts (older than 30 days)
        LocalDateTime cutoffDate = now.minusDays(30);
        int deleted = cartRepository.deleteOldExpiredCarts(cutoffDate);
        logger.info("Deleted {} old expired carts", deleted);
    }
    
    @Override
    public CartResponse extendCartExpiration(Long userId, String sessionId, int days) {
        logger.info("Extending cart expiration by {} days for user: {} or session: {}", days, userId, sessionId);
        
        Cart cart = getActiveCart(userId, sessionId);
        cart.extendExpiration(days);
        cart = cartRepository.save(cart);
        
        return new CartResponse(cart);
    }
    
    // Helper methods
    
    private Cart getActiveCart(Long userId, String sessionId) {
        if (userId != null) {
            return cartRepository.findActiveCartByUserId(userId)
                    .filter(cart -> !cart.isExpired())
                    .orElseGet(() -> cartRepository.save(new Cart(userId)));
        } else if (sessionId != null) {
            return cartRepository.findActiveCartBySessionId(sessionId)
                    .filter(cart -> !cart.isExpired())
                    .orElseGet(() -> cartRepository.save(new Cart(sessionId)));
        } else {
            throw new RuntimeException("Either userId or sessionId must be provided");
        }
    }
    
    private ItemServiceClient.ItemDto validateAndGetItem(Long itemId) {
        try {
            ItemServiceClient.ItemDto item = itemServiceClient.getItemById(itemId).getBody();
            if (item == null) {
                throw new RuntimeException("Item not found: " + itemId);
            }
            return item;
        } catch (FeignException e) {
            logger.error("Error fetching item {}: {}", itemId, e.getMessage());
            throw new RuntimeException("Unable to validate item: " + itemId);
        }
    }
    
    private void validateStockAvailability(Long itemId, Integer requestedQuantity) {
        try {
            ItemServiceClient.ItemAvailabilityDto availability = 
                    itemServiceClient.checkItemAvailability(itemId).getBody();
            
            if (availability == null || !availability.isAvailable()) {
                throw new RuntimeException("Item is not available: " + itemId);
            }
            
            if (requestedQuantity > availability.getAvailableQuantity()) {
                throw new RuntimeException("Requested quantity (" + requestedQuantity + 
                        ") exceeds available stock (" + availability.getAvailableQuantity() + ") for item: " + itemId);
            }
        } catch (FeignException e) {
            logger.warn("Unable to check stock availability for item {}: {}", itemId, e.getMessage());
            // Continue without stock validation if service is unavailable
        }
    }
} 