import React, { useState, useEffect } from 'react';
import {
  Box,
  Typography,
  Card,
  CardContent,
  Button,
  Stepper,
  Step,
  StepLabel,
  TextField,
  FormControl,
  FormLabel,
  RadioGroup,
  FormControlLabel,
  Radio,
  Divider,
  Alert,
  Skeleton,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Chip,
  IconButton,
} from '@mui/material';
import {
  ArrowBack,
  ArrowForward,
  Add,
  Edit,
  CreditCard,
  AccountBalance,
  Phone,
  LocalShipping,
  Security,
  CheckCircle,
} from '@mui/icons-material';
import { useNavigate } from 'react-router-dom';
import { useForm, Controller } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import * as yup from 'yup';
import { apiService } from '../../services/api';
import { Cart, UserAddress, Order } from '../../types';
import { toast } from 'react-toastify';
import { useAuth } from '../../contexts/AuthContext';

const steps = ['Shipping Address', 'Payment Method', 'Review Order'];

const addressSchema = yup.object({
  recipientName: yup.string().required('Recipient name is required'),
  phoneNumber: yup.string().required('Phone number is required'),
  addressLine1: yup.string().required('Address is required'),
  city: yup.string().required('City is required'),
  state: yup.string().required('State is required'),
  postalCode: yup.string().required('Postal code is required'),
  country: yup.string().required('Country is required'),
});

interface AddressForm {
  recipientName: string;
  phoneNumber: string;
  addressLine1: string;
  addressLine2?: string;
  landmark?: string;
  city: string;
  state: string;
  postalCode: string;
  country: string;
}

const CheckoutPage: React.FC = () => {
  const [activeStep, setActiveStep] = useState(0);
  const [cart, setCart] = useState<Cart | null>(null);
  const [addresses, setAddresses] = useState<UserAddress[]>([]);
  const [selectedAddressId, setSelectedAddressId] = useState<number | null>(null);
  const [paymentMethod, setPaymentMethod] = useState('');
  const [isLoading, setIsLoading] = useState(true);
  const [isProcessing, setIsProcessing] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [addressDialogOpen, setAddressDialogOpen] = useState(false);
  const [orderSuccess, setOrderSuccess] = useState(false);
  const [createdOrder, setCreatedOrder] = useState<Order | null>(null);
  
  const { user } = useAuth();
  const navigate = useNavigate();

  const {
    control,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<AddressForm>({
    resolver: yupResolver(addressSchema),
    defaultValues: {
      country: 'United States',
    },
  });

  useEffect(() => {
    if (!user) {
      navigate('/auth/login');
      return;
    }
    
    loadCheckoutData();
  }, [user, navigate]);

  const loadCheckoutData = async () => {
    try {
      setIsLoading(true);
      setError(null);

      const [cartData, addressesData] = await Promise.all([
        apiService.getCart(),
        apiService.getUserAddresses(),
      ]);

      if (!cartData || cartData.items.length === 0) {
        navigate('/cart');
        return;
      }

      setCart(cartData);
      setAddresses(addressesData);
      
      // Auto-select default address
      const defaultAddress = addressesData.find(addr => addr.isDefault);
      if (defaultAddress) {
        setSelectedAddressId(defaultAddress.id);
      }
    } catch (error: any) {
      console.error('Failed to load checkout data:', error);
      setError('Failed to load checkout data. Please try again.');
    } finally {
      setIsLoading(false);
    }
  };

  const handleNext = () => {
    if (activeStep === 0 && !selectedAddressId) {
      toast.error('Please select a shipping address');
      return;
    }
    
    if (activeStep === 1 && !paymentMethod) {
      toast.error('Please select a payment method');
      return;
    }

    setActiveStep((prevActiveStep) => prevActiveStep + 1);
  };

  const handleBack = () => {
    setActiveStep((prevActiveStep) => prevActiveStep - 1);
  };

  const handleAddAddress = async (addressData: AddressForm) => {
    try {
      const newAddress = await apiService.addUserAddress({
        ...addressData,
        addressType: 'SHIPPING' as const,
        isDefault: addresses.length === 0,
        isActive: true,
        usageCount: 0,
        fullAddress: `${addressData.addressLine1}, ${addressData.city}, ${addressData.state} ${addressData.postalCode}`,
      });
      
      setAddresses(prev => [...prev, newAddress]);
      setSelectedAddressId(newAddress.id);
      setAddressDialogOpen(false);
      reset();
      toast.success('Address added successfully');
    } catch (error: any) {
      toast.error('Failed to add address. Please try again.');
    }
  };

  const handlePlaceOrder = async () => {
    if (!cart || !selectedAddressId || !paymentMethod) {
      toast.error('Please complete all checkout steps');
      return;
    }

    setIsProcessing(true);
    
    try {
      const selectedAddress = addresses.find(addr => addr.id === selectedAddressId);
      if (!selectedAddress) {
        throw new Error('Selected address not found');
      }

      const orderData = {
        shippingAddress: {
          recipientName: selectedAddress.recipientName,
          phoneNumber: selectedAddress.phoneNumber || '',
          addressLine1: selectedAddress.addressLine1,
          addressLine2: selectedAddress.addressLine2,
          landmark: selectedAddress.landmark,
          city: selectedAddress.city,
          state: selectedAddress.state,
          postalCode: selectedAddress.postalCode,
          country: selectedAddress.country,
        },
        paymentMethod: paymentMethod as 'CREDIT_CARD' | 'DEBIT_CARD' | 'UPI' | 'NET_BANKING' | 'WALLET' | 'COD',
        billingAddress: selectedAddress, // Use same as shipping for simplicity
      };

      const order = await apiService.createOrder(orderData);
      setCreatedOrder(order);
      setOrderSuccess(true);
      
      // Clear cart after successful order
      await apiService.clearCart();
      
      toast.success('Order placed successfully!');
    } catch (error: any) {
      console.error('Failed to place order:', error);
      toast.error('Failed to place order. Please try again.');
    } finally {
      setIsProcessing(false);
    }
  };

  const AddressStep = () => (
    <Box>
      <Typography variant="h6" gutterBottom>
        Select Shipping Address
      </Typography>
      
      <Box sx={{ mb: 3 }}>
        {addresses.map((address) => (
          <Card
            key={address.id}
            sx={{
              mb: 2,
              cursor: 'pointer',
              border: selectedAddressId === address.id ? '2px solid' : '1px solid',
              borderColor: selectedAddressId === address.id ? 'primary.main' : 'grey.300',
            }}
            onClick={() => setSelectedAddressId(address.id)}
          >
            <CardContent>
              <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
                <Box>
                  <Typography variant="subtitle1" fontWeight="bold">
                    {address.recipientName}
                  </Typography>
                  <Typography variant="body2" color="text.secondary">
                    {address.fullAddress}
                  </Typography>
                  {address.phoneNumber && (
                    <Typography variant="body2" color="text.secondary">
                      Phone: {address.phoneNumber}
                    </Typography>
                  )}
                  <Box sx={{ mt: 1 }}>
                    <Chip label={address.addressType} size="small" variant="outlined" />
                    {address.isDefault && (
                      <Chip label="Default" size="small" color="primary" sx={{ ml: 1 }} />
                    )}
                  </Box>
                </Box>
                <IconButton size="small">
                  <Edit />
                </IconButton>
              </Box>
            </CardContent>
          </Card>
        ))}
        
        <Button
          variant="outlined"
          startIcon={<Add />}
          onClick={() => setAddressDialogOpen(true)}
          fullWidth
        >
          Add New Address
        </Button>
      </Box>
    </Box>
  );

  const PaymentStep = () => (
    <Box>
      <Typography variant="h6" gutterBottom>
        Select Payment Method
      </Typography>
      
      <FormControl component="fieldset" fullWidth>
        <RadioGroup
          value={paymentMethod}
          onChange={(e) => setPaymentMethod(e.target.value)}
        >
          <Card sx={{ mb: 2 }}>
            <CardContent>
              <FormControlLabel
                value="CREDIT_CARD"
                control={<Radio />}
                label={
                  <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                    <CreditCard />
                    <Box>
                      <Typography variant="body1">Credit Card</Typography>
                      <Typography variant="body2" color="text.secondary">
                        Visa, MasterCard, American Express
                      </Typography>
                    </Box>
                  </Box>
                }
              />
            </CardContent>
          </Card>

          <Card sx={{ mb: 2 }}>
            <CardContent>
              <FormControlLabel
                value="DEBIT_CARD"
                control={<Radio />}
                label={
                  <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                    <CreditCard />
                    <Box>
                      <Typography variant="body1">Debit Card</Typography>
                      <Typography variant="body2" color="text.secondary">
                        All major debit cards accepted
                      </Typography>
                    </Box>
                  </Box>
                }
              />
            </CardContent>
          </Card>

          <Card sx={{ mb: 2 }}>
            <CardContent>
              <FormControlLabel
                value="UPI"
                control={<Radio />}
                label={
                  <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                    <Phone />
                    <Box>
                      <Typography variant="body1">UPI</Typography>
                      <Typography variant="body2" color="text.secondary">
                        Pay using UPI apps like GPay, PhonePe, Paytm
                      </Typography>
                    </Box>
                  </Box>
                }
              />
            </CardContent>
          </Card>

          <Card sx={{ mb: 2 }}>
            <CardContent>
              <FormControlLabel
                value="NET_BANKING"
                control={<Radio />}
                label={
                  <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                    <AccountBalance />
                    <Box>
                      <Typography variant="body1">Net Banking</Typography>
                      <Typography variant="body2" color="text.secondary">
                        All major banks supported
                      </Typography>
                    </Box>
                  </Box>
                }
              />
            </CardContent>
          </Card>

          <Card sx={{ mb: 2 }}>
            <CardContent>
              <FormControlLabel
                value="COD"
                control={<Radio />}
                label={
                  <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                    <LocalShipping />
                    <Box>
                      <Typography variant="body1">Cash on Delivery</Typography>
                      <Typography variant="body2" color="text.secondary">
                        Pay when you receive your order
                      </Typography>
                    </Box>
                  </Box>
                }
              />
            </CardContent>
          </Card>
        </RadioGroup>
      </FormControl>
    </Box>
  );

  const ReviewStep = () => {
    const selectedAddress = addresses.find(addr => addr.id === selectedAddressId);
    
    return (
      <Box>
        <Typography variant="h6" gutterBottom>
          Review Your Order
        </Typography>
        
        {/* Shipping Address */}
        <Card sx={{ mb: 3 }}>
          <CardContent>
            <Typography variant="subtitle1" fontWeight="bold" gutterBottom>
              Shipping Address
            </Typography>
            {selectedAddress && (
              <Box>
                <Typography variant="body1">{selectedAddress.recipientName}</Typography>
                <Typography variant="body2" color="text.secondary">
                  {selectedAddress.fullAddress}
                </Typography>
                <Typography variant="body2" color="text.secondary">
                  Phone: {selectedAddress.phoneNumber}
                </Typography>
              </Box>
            )}
          </CardContent>
        </Card>

        {/* Payment Method */}
        <Card sx={{ mb: 3 }}>
          <CardContent>
            <Typography variant="subtitle1" fontWeight="bold" gutterBottom>
              Payment Method
            </Typography>
            <Typography variant="body1">
              {paymentMethod.replace('_', ' ').toLowerCase().replace(/\b\w/g, l => l.toUpperCase())}
            </Typography>
          </CardContent>
        </Card>

        {/* Order Items */}
        <Card sx={{ mb: 3 }}>
          <CardContent>
            <Typography variant="subtitle1" fontWeight="bold" gutterBottom>
              Order Items
            </Typography>
            {cart?.items.map((item) => (
              <Box key={item.id} sx={{ display: 'flex', justifyContent: 'space-between', mb: 2 }}>
                <Box sx={{ display: 'flex', gap: 2 }}>
                  <Box
                    component="img"
                    src={item.productImage || 'https://via.placeholder.com/60x60'}
                    alt={item.productName}
                    sx={{ width: 60, height: 60, objectFit: 'cover', borderRadius: 1 }}
                  />
                  <Box>
                    <Typography variant="body1">{item.productName}</Typography>
                    <Typography variant="body2" color="text.secondary">
                      Qty: {item.quantity}
                    </Typography>
                  </Box>
                </Box>
                <Typography variant="body1" fontWeight="bold">
                  ${item.subtotal.toFixed(2)}
                </Typography>
              </Box>
            ))}
          </CardContent>
        </Card>

        {/* Order Summary */}
        <Card>
          <CardContent>
            <Typography variant="subtitle1" fontWeight="bold" gutterBottom>
              Order Summary
            </Typography>
            <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1 }}>
              <Typography variant="body2">Subtotal:</Typography>
              <Typography variant="body2">${cart?.totalAmount.toFixed(2)}</Typography>
            </Box>
            {cart && cart.discountAmount > 0 && (
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
              <Typography variant="body2">$0.00</Typography>
            </Box>
            <Divider sx={{ my: 2 }} />
            <Box sx={{ display: 'flex', justifyContent: 'space-between' }}>
              <Typography variant="h6" fontWeight="bold">Total:</Typography>
              <Typography variant="h6" fontWeight="bold" color="primary.main">
                ${cart?.finalAmount.toFixed(2)}
              </Typography>
            </Box>
          </CardContent>
        </Card>

        {/* Security Notice */}
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mt: 3, p: 2, bgcolor: 'grey.50', borderRadius: 1 }}>
          <Security color="success" />
          <Typography variant="body2">
            Your payment information is encrypted and secure
          </Typography>
        </Box>
      </Box>
    );
  };

  if (orderSuccess) {
    return (
      <Box sx={{ p: 3, textAlign: 'center', maxWidth: 600, mx: 'auto' }}>
        <CheckCircle color="success" sx={{ fontSize: 80, mb: 2 }} />
        <Typography variant="h4" gutterBottom>
          Order Placed Successfully!
        </Typography>
        <Typography variant="body1" color="text.secondary" gutterBottom>
          Thank you for your order. We'll send you a confirmation email shortly.
        </Typography>
        
        {createdOrder && (
          <Card sx={{ mt: 3, p: 3 }}>
            <Typography variant="h6" gutterBottom>
              Order Details
            </Typography>
            <Typography variant="body1">
              Order Number: <strong>{createdOrder.orderNumber}</strong>
            </Typography>
            <Typography variant="body1">
              Total Amount: <strong>${createdOrder.finalAmount.toFixed(2)}</strong>
            </Typography>
            <Typography variant="body2" color="text.secondary" sx={{ mt: 1 }}>
              Estimated delivery: 3-5 business days
            </Typography>
          </Card>
        )}

        <Box sx={{ mt: 4, display: 'flex', gap: 2, justifyContent: 'center' }}>
          <Button
            variant="contained"
            onClick={() => navigate('/user/orders')}
          >
            View Orders
          </Button>
          <Button
            variant="outlined"
            onClick={() => navigate('/products')}
          >
            Continue Shopping
          </Button>
        </Box>
      </Box>
    );
  }

  if (error) {
    return (
      <Box sx={{ p: 3 }}>
        <Alert severity="error" action={
          <Button color="inherit" size="small" onClick={loadCheckoutData}>
            Retry
          </Button>
        }>
          {error}
        </Alert>
      </Box>
    );
  }

  if (isLoading) {
    return (
      <Box sx={{ p: 3 }}>
        <Skeleton variant="text" height={40} width={200} sx={{ mb: 3 }} />
        <Skeleton variant="rectangular" height={400} />
      </Box>
    );
  }

  return (
    <Box sx={{ p: 3, maxWidth: 1200, mx: 'auto' }}>
      {/* Header */}
      <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, mb: 4 }}>
        <Button
          startIcon={<ArrowBack />}
          onClick={() => navigate('/cart')}
        >
          Back to Cart
        </Button>
        <Typography variant="h4" component="h1">
          Checkout
        </Typography>
      </Box>

      {/* Stepper */}
      <Stepper activeStep={activeStep} sx={{ mb: 4 }}>
        {steps.map((label) => (
          <Step key={label}>
            <StepLabel>{label}</StepLabel>
          </Step>
        ))}
      </Stepper>

      {/* Step Content */}
      <Box sx={{ mb: 4 }}>
        {activeStep === 0 && <AddressStep />}
        {activeStep === 1 && <PaymentStep />}
        {activeStep === 2 && <ReviewStep />}
      </Box>

      {/* Navigation Buttons */}
      <Box sx={{ display: 'flex', justifyContent: 'space-between' }}>
        <Button
          onClick={handleBack}
          disabled={activeStep === 0}
        >
          Back
        </Button>
        
        {activeStep === steps.length - 1 ? (
          <Button
            variant="contained"
            onClick={handlePlaceOrder}
            disabled={isProcessing}
            size="large"
          >
            {isProcessing ? 'Processing...' : 'Place Order'}
          </Button>
        ) : (
          <Button
            variant="contained"
            onClick={handleNext}
            endIcon={<ArrowForward />}
          >
            Next
          </Button>
        )}
      </Box>

      {/* Add Address Dialog */}
      <Dialog
        open={addressDialogOpen}
        onClose={() => setAddressDialogOpen(false)}
        maxWidth="sm"
        fullWidth
      >
        <DialogTitle>Add New Address</DialogTitle>
        <DialogContent>
          <Box component="form" onSubmit={handleSubmit(handleAddAddress)} sx={{ mt: 2 }}>
            <Controller
              name="recipientName"
              control={control}
              render={({ field }) => (
                <TextField
                  {...field}
                  label="Recipient Name"
                  fullWidth
                  margin="normal"
                  error={!!errors.recipientName}
                  helperText={errors.recipientName?.message}
                />
              )}
            />
            
            <Controller
              name="phoneNumber"
              control={control}
              render={({ field }) => (
                <TextField
                  {...field}
                  label="Phone Number"
                  fullWidth
                  margin="normal"
                  error={!!errors.phoneNumber}
                  helperText={errors.phoneNumber?.message}
                />
              )}
            />
            
            <Controller
              name="addressLine1"
              control={control}
              render={({ field }) => (
                <TextField
                  {...field}
                  label="Address Line 1"
                  fullWidth
                  margin="normal"
                  error={!!errors.addressLine1}
                  helperText={errors.addressLine1?.message}
                />
              )}
            />
            
            <Controller
              name="addressLine2"
              control={control}
              render={({ field }) => (
                <TextField
                  {...field}
                  label="Address Line 2 (Optional)"
                  fullWidth
                  margin="normal"
                />
              )}
            />
            
            <Controller
              name="landmark"
              control={control}
              render={({ field }) => (
                <TextField
                  {...field}
                  label="Landmark (Optional)"
                  fullWidth
                  margin="normal"
                />
              )}
            />
            
            <Box sx={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 2, mt: 2 }}>
              <Controller
                name="city"
                control={control}
                render={({ field }) => (
                  <TextField
                    {...field}
                    label="City"
                    fullWidth
                    error={!!errors.city}
                    helperText={errors.city?.message}
                  />
                )}
              />
              
              <Controller
                name="state"
                control={control}
                render={({ field }) => (
                  <TextField
                    {...field}
                    label="State"
                    fullWidth
                    error={!!errors.state}
                    helperText={errors.state?.message}
                  />
                )}
              />
            </Box>
            
            <Box sx={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 2, mt: 2 }}>
              <Controller
                name="postalCode"
                control={control}
                render={({ field }) => (
                  <TextField
                    {...field}
                    label="Postal Code"
                    fullWidth
                    error={!!errors.postalCode}
                    helperText={errors.postalCode?.message}
                  />
                )}
              />
              
              <Controller
                name="country"
                control={control}
                render={({ field }) => (
                  <TextField
                    {...field}
                    label="Country"
                    fullWidth
                    error={!!errors.country}
                    helperText={errors.country?.message}
                  />
                )}
              />
            </Box>
          </Box>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setAddressDialogOpen(false)}>
            Cancel
          </Button>
          <Button
            onClick={handleSubmit(handleAddAddress)}
            variant="contained"
          >
            Add Address
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};

export default CheckoutPage;
