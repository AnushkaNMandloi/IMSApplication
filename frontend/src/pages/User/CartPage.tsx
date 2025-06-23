import React, { useState, useEffect } from 'react';
import {
  Box,
  Typography,
  Card,
  CardContent,
  Button,
  IconButton,
  TextField,
  Divider,
  Alert,
  Skeleton,
  Chip,
  Badge,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
} from '@mui/material';
import {
  Add,
  Remove,
  Delete,
  ShoppingCartCheckout,
  ArrowBack,
  Refresh,
  LocalOffer,
  Security,
  LocalShipping,
} from '@mui/icons-material';
import { useNavigate, Link as RouterLink } from 'react-router-dom';
import { apiService } from '../../services/api';
import { Cart, CartItem } from '../../types';
import { toast } from 'react-toastify';
import { useAuth } from '../../contexts/AuthContext';

const CartPage: React.FC = () => {
  const [cart, setCart] = useState<Cart | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [updatingItems, setUpdatingItems] = useState<Set<number>>(new Set());
  const [couponCode, setCouponCode] = useState('');
  const [appliedCoupon, setAppliedCoupon] = useState<string | null>(null);
  const [deleteConfirmOpen, setDeleteConfirmOpen] = useState(false);
  const [itemToDelete, setItemToDelete] = useState<number | null>(null);
  const { user } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    loadCart();
  }, []);

  const loadCart = async () => {
    try {
      setIsLoading(true);
      setError(null);
      const cartData = await apiService.getCart();
      setCart(cartData);
    } catch (error: any) {
      console.error('Failed to load cart:', error);
      setError('Failed to load cart. Please try again.');
    } finally {
      setIsLoading(false);
    }
  };

  const handleUpdateQuantity = async (itemId: number, newQuantity: number) => {
    if (newQuantity < 1) return;

    setUpdatingItems(prev => new Set(prev).add(itemId));
    
    try {
      await apiService.updateCartItem(itemId, newQuantity);
      await loadCart(); // Reload cart to get updated totals
      toast.success('Quantity updated');
    } catch (error: any) {
      toast.error('Failed to update quantity. Please try again.');
    } finally {
      setUpdatingItems(prev => {
        const newSet = new Set(prev);
        newSet.delete(itemId);
        return newSet;
      });
    }
  };

  const handleRemoveItem = async (itemId: number) => {
    try {
      await apiService.removeFromCart(itemId);
      await loadCart();
      toast.success('Item removed from cart');
      setDeleteConfirmOpen(false);
      setItemToDelete(null);
    } catch (error: any) {
      toast.error('Failed to remove item. Please try again.');
    }
  };

  const handleClearCart = async () => {
    try {
      await apiService.clearCart();
      await loadCart();
      toast.success('Cart cleared');
    } catch (error: any) {
      toast.error('Failed to clear cart. Please try again.');
    }
  };

  const handleApplyCoupon = async () => {
    if (!couponCode.trim()) {
      toast.error('Please enter a coupon code');
      return;
    }

    try {
      // Mock coupon application - replace with actual API call
      if (couponCode.toLowerCase() === 'save10') {
        setAppliedCoupon(couponCode);
        toast.success('Coupon applied successfully!');
        setCouponCode('');
      } else {
        toast.error('Invalid coupon code');
      }
    } catch (error: any) {
      toast.error('Failed to apply coupon. Please try again.');
    }
  };

  const handleRemoveCoupon = () => {
    setAppliedCoupon(null);
    toast.success('Coupon removed');
  };

  const handleValidateCart = async () => {
    try {
      const validatedCart = await apiService.validateCart();
      setCart(validatedCart);
      toast.success('Cart validated');
    } catch (error: any) {
      toast.error('Some items in your cart are no longer available');
      await loadCart();
    }
  };

  const handleCheckout = () => {
    if (!user) {
      toast.error('Please log in to proceed with checkout');
      navigate('/auth/login');
      return;
    }

    if (!cart || cart.items.length === 0) {
      toast.error('Your cart is empty');
      return;
    }

    // Navigate to checkout page
    navigate('/checkout');
  };

  const calculateSavings = () => {
    if (!cart) return 0;
    
    let savings = 0;
    cart.items.forEach(item => {
      // Calculate savings if there were original prices
      // This is a mock calculation - replace with actual logic
      const originalPrice = item.price * 1.2; // Assume 20% discount
      savings += (originalPrice - item.price) * item.quantity;
    });
    
    return savings;
  };

  const CartItemCard = ({ item }: { item: CartItem }) => {
    const isUpdating = updatingItems.has(item.id);
    
    return (
      <Card sx={{ mb: 2 }}>
        <CardContent>
          <Box sx={{ display: 'flex', gap: 2 }}>
            {/* Product Image */}
            <Box
              component="img"
              src={item.productImage || 'https://via.placeholder.com/120x120'}
              alt={item.productName}
              sx={{
                width: 120,
                height: 120,
                objectFit: 'cover',
                borderRadius: 1,
                cursor: 'pointer',
              }}
              onClick={() => navigate(`/products/${item.productId}`)}
            />

            {/* Product Details */}
            <Box sx={{ flexGrow: 1 }}>
              <Typography
                variant="h6"
                component="h3"
                sx={{ cursor: 'pointer', '&:hover': { color: 'primary.main' } }}
                onClick={() => navigate(`/products/${item.productId}`)}
              >
                {item.productName}
              </Typography>
              
              <Typography variant="body2" color="text.secondary" gutterBottom>
                Sold by {item.sellerName}
              </Typography>

              {!item.isAvailable && (
                <Chip
                  label="Out of Stock"
                  color="error"
                  size="small"
                  sx={{ mb: 1 }}
                />
              )}

              <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, mt: 2 }}>
                {/* Quantity Controls */}
                <Box sx={{ display: 'flex', alignItems: 'center', border: '1px solid', borderColor: 'grey.300', borderRadius: 1 }}>
                  <IconButton
                    size="small"
                    onClick={() => handleUpdateQuantity(item.id, item.quantity - 1)}
                    disabled={item.quantity <= 1 || isUpdating || !item.isAvailable}
                  >
                    <Remove />
                  </IconButton>
                  <Typography sx={{ px: 2, minWidth: 40, textAlign: 'center' }}>
                    {item.quantity}
                  </Typography>
                  <IconButton
                    size="small"
                    onClick={() => handleUpdateQuantity(item.id, item.quantity + 1)}
                    disabled={item.quantity >= item.maxQuantity || isUpdating || !item.isAvailable}
                  >
                    <Add />
                  </IconButton>
                </Box>

                {/* Remove Button */}
                <IconButton
                  color="error"
                  onClick={() => {
                    setItemToDelete(item.id);
                    setDeleteConfirmOpen(true);
                  }}
                  disabled={isUpdating}
                >
                  <Delete />
                </IconButton>
              </Box>

              {item.quantity >= item.maxQuantity && (
                <Typography variant="caption" color="warning.main" sx={{ mt: 1, display: 'block' }}>
                  Maximum quantity reached
                </Typography>
              )}
            </Box>

            {/* Price */}
            <Box sx={{ textAlign: 'right' }}>
              <Typography variant="h6" color="primary.main" fontWeight="bold">
                ${item.subtotal.toFixed(2)}
              </Typography>
              <Typography variant="body2" color="text.secondary">
                ${item.price.toFixed(2)} each
              </Typography>
              <Typography variant="caption" color="text.secondary">
                Added {new Date(item.addedAt).toLocaleDateString()}
              </Typography>
            </Box>
          </Box>
        </CardContent>
      </Card>
    );
  };

  if (error) {
    return (
      <Box sx={{ p: 3 }}>
        <Alert severity="error" action={
          <Button color="inherit" size="small" onClick={loadCart}>
            Retry
          </Button>
        }>
          {error}
        </Alert>
      </Box>
    );
  }

  return (
    <Box sx={{ p: 3 }}>
      {/* Header */}
      <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, mb: 3 }}>
        <Button
          startIcon={<ArrowBack />}
          onClick={() => navigate(-1)}
        >
          Continue Shopping
        </Button>
        <Typography variant="h4" component="h1" sx={{ flexGrow: 1 }}>
          Shopping Cart
        </Typography>
        <Button
          startIcon={<Refresh />}
          onClick={handleValidateCart}
          variant="outlined"
          size="small"
        >
          Validate Cart
        </Button>
      </Box>

      {isLoading ? (
        <Box>
          {Array.from({ length: 3 }).map((_, index) => (
            <Card key={index} sx={{ mb: 2 }}>
              <CardContent>
                <Box sx={{ display: 'flex', gap: 2 }}>
                  <Skeleton variant="rectangular" width={120} height={120} />
                  <Box sx={{ flexGrow: 1 }}>
                    <Skeleton variant="text" height={32} width="60%" />
                    <Skeleton variant="text" height={24} width="40%" />
                    <Skeleton variant="rectangular" height={40} width={120} sx={{ mt: 2 }} />
                  </Box>
                  <Box>
                    <Skeleton variant="text" height={32} width={80} />
                    <Skeleton variant="text" height={24} width={60} />
                  </Box>
                </Box>
              </CardContent>
            </Card>
          ))}
        </Box>
      ) : !cart || cart.items.length === 0 ? (
        <Box sx={{ textAlign: 'center', py: 8 }}>
          <Typography variant="h5" gutterBottom>
            Your cart is empty
          </Typography>
          <Typography variant="body1" color="text.secondary" gutterBottom>
            Add some products to get started
          </Typography>
          <Button
            variant="contained"
            component={RouterLink}
            to="/products"
            sx={{ mt: 2 }}
          >
            Continue Shopping
          </Button>
        </Box>
      ) : (
        <Box sx={{ display: 'grid', gridTemplateColumns: { xs: '1fr', lg: '2fr 1fr' }, gap: 4 }}>
          {/* Cart Items */}
          <Box>
            <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
              <Typography variant="h6">
                Cart Items ({cart.totalItems})
              </Typography>
              <Button
                color="error"
                onClick={handleClearCart}
                size="small"
              >
                Clear Cart
              </Button>
            </Box>

            {cart.items.map((item) => (
              <CartItemCard key={item.id} item={item} />
            ))}
          </Box>

          {/* Order Summary */}
          <Box>
            <Card sx={{ position: 'sticky', top: 20 }}>
              <CardContent>
                <Typography variant="h6" gutterBottom>
                  Order Summary
                </Typography>

                {/* Coupon Section */}
                <Box sx={{ mb: 3 }}>
                  <Typography variant="body2" gutterBottom>
                    Have a coupon?
                  </Typography>
                  <Box sx={{ display: 'flex', gap: 1, mb: 1 }}>
                    <TextField
                      size="small"
                      placeholder="Enter coupon code"
                      value={couponCode}
                      onChange={(e) => setCouponCode(e.target.value)}
                      sx={{ flexGrow: 1 }}
                    />
                    <Button
                      variant="outlined"
                      onClick={handleApplyCoupon}
                      startIcon={<LocalOffer />}
                    >
                      Apply
                    </Button>
                  </Box>
                  
                  {appliedCoupon && (
                    <Chip
                      label={`Coupon: ${appliedCoupon}`}
                      onDelete={handleRemoveCoupon}
                      color="success"
                      size="small"
                    />
                  )}
                </Box>

                <Divider sx={{ mb: 2 }} />

                {/* Price Breakdown */}
                <Box sx={{ mb: 2 }}>
                  <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1 }}>
                    <Typography variant="body2">Subtotal ({cart.totalItems} items):</Typography>
                    <Typography variant="body2">${cart.totalAmount.toFixed(2)}</Typography>
                  </Box>
                  
                  {cart.discountAmount > 0 && (
                    <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1 }}>
                      <Typography variant="body2" color="success.main">Discount:</Typography>
                      <Typography variant="body2" color="success.main">
                        -${cart.discountAmount.toFixed(2)}
                      </Typography>
                    </Box>
                  )}

                  <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1 }}>
                    <Typography variant="body2">Shipping:</Typography>
                    <Typography variant="body2" color="success.main">FREE</Typography>
                  </Box>

                  <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1 }}>
                    <Typography variant="body2">Tax:</Typography>
                    <Typography variant="body2">Calculated at checkout</Typography>
                  </Box>
                </Box>

                <Divider sx={{ mb: 2 }} />

                {/* Total */}
                <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 3 }}>
                  <Typography variant="h6" fontWeight="bold">Total:</Typography>
                  <Typography variant="h6" fontWeight="bold" color="primary.main">
                    ${cart.finalAmount.toFixed(2)}
                  </Typography>
                </Box>

                {/* Savings */}
                {calculateSavings() > 0 && (
                  <Box sx={{ mb: 3, p: 2, bgcolor: 'success.light', borderRadius: 1 }}>
                    <Typography variant="body2" color="success.dark">
                      🎉 You're saving ${calculateSavings().toFixed(2)} on this order!
                    </Typography>
                  </Box>
                )}

                {/* Security & Delivery Info */}
                <Box sx={{ mb: 3 }}>
                  <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 1 }}>
                    <Security fontSize="small" color="success" />
                    <Typography variant="body2">Secure checkout</Typography>
                  </Box>
                  <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                    <LocalShipping fontSize="small" color="primary" />
                    <Typography variant="body2">Free delivery on orders over $50</Typography>
                  </Box>
                </Box>

                {/* Checkout Button */}
                <Button
                  variant="contained"
                  fullWidth
                  size="large"
                  startIcon={<ShoppingCartCheckout />}
                  onClick={handleCheckout}
                  disabled={cart.items.some(item => !item.isAvailable)}
                >
                  Proceed to Checkout
                </Button>

                {cart.items.some(item => !item.isAvailable) && (
                  <Alert severity="warning" sx={{ mt: 2 }}>
                    Some items in your cart are no longer available. Please remove them to continue.
                  </Alert>
                )}

                {/* Continue Shopping */}
                <Button
                  variant="text"
                  fullWidth
                  component={RouterLink}
                  to="/products"
                  sx={{ mt: 1 }}
                >
                  Continue Shopping
                </Button>
              </CardContent>
            </Card>
          </Box>
        </Box>
      )}

      {/* Delete Confirmation Dialog */}
      <Dialog
        open={deleteConfirmOpen}
        onClose={() => setDeleteConfirmOpen(false)}
      >
        <DialogTitle>Remove Item</DialogTitle>
        <DialogContent>
          <Typography>
            Are you sure you want to remove this item from your cart?
          </Typography>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setDeleteConfirmOpen(false)}>
            Cancel
          </Button>
          <Button
            onClick={() => itemToDelete && handleRemoveItem(itemToDelete)}
            color="error"
            variant="contained"
          >
            Remove
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};

export default CartPage;
