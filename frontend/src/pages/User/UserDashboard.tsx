import React, { useState, useEffect } from 'react';
import {
  Box,
  Typography,
  Card,
  CardContent,
  Button,
  Avatar,
  Chip,
  IconButton,
  Skeleton,
  Alert,
} from '@mui/material';
import {
  ShoppingCart,
  FavoriteOutlined,
  LocalShipping,
  AccountCircle,
  TrendingUp,
  Star,
  Receipt,
  Add,
  Refresh,
} from '@mui/icons-material';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../contexts/AuthContext';
import { apiService } from '../../services/api';
import { Order, Product, UserProfile } from '../../types';
import { toast } from 'react-toastify';

const UserDashboard: React.FC = () => {
  const [isLoading, setIsLoading] = useState(true);
  const [userProfile, setUserProfile] = useState<UserProfile | null>(null);
  const [recentOrders, setRecentOrders] = useState<Order[]>([]);
  const [featuredProducts, setFeaturedProducts] = useState<Product[]>([]);
  const [error, setError] = useState<string | null>(null);
  const { user } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    loadDashboardData();
  }, []);

  const loadDashboardData = async () => {
    try {
      setIsLoading(true);
      setError(null);

      const [profileData, ordersData, productsData] = await Promise.all([
        apiService.getUserProfile().catch(() => null),
        apiService.getOrders({}, 0, 5).catch(() => ({ content: [] })),
        apiService.getFeaturedProducts().catch(() => []),
      ]);

      setUserProfile(profileData);
      setRecentOrders(ordersData.content || []);
      setFeaturedProducts(productsData.slice(0, 4));
    } catch (error: any) {
      console.error('Failed to load dashboard data:', error);
      setError('Failed to load dashboard data. Please try again.');
    } finally {
      setIsLoading(false);
    }
  };

  const handleRefresh = () => {
    loadDashboardData();
    toast.success('Dashboard refreshed!');
  };

  const getOrderStatusColor = (status: string) => {
    switch (status) {
      case 'DELIVERED':
        return 'success';
      case 'SHIPPED':
        return 'info';
      case 'PROCESSING':
        return 'warning';
      case 'CANCELLED':
        return 'error';
      default:
        return 'default';
    }
  };

  const getMembershipTierColor = (tier: string) => {
    switch (tier) {
      case 'DIAMOND':
        return '#E1F5FE';
      case 'PLATINUM':
        return '#F3E5F5';
      case 'GOLD':
        return '#FFF8E1';
      case 'SILVER':
        return '#F5F5F5';
      default:
        return '#FAFAFA';
    }
  };

  if (error) {
    return (
      <Box sx={{ p: 3 }}>
        <Alert severity="error" action={
          <Button color="inherit" size="small" onClick={loadDashboardData}>
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
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 4 }}>
        <Box>
          <Typography variant="h4" component="h1" gutterBottom>
            Welcome back, {userProfile?.firstName || user?.username}! 👋
          </Typography>
          <Typography variant="body1" color="text.secondary">
            Here's what's happening with your account today.
          </Typography>
        </Box>
        <IconButton onClick={handleRefresh} color="primary">
          <Refresh />
        </IconButton>
      </Box>

      {/* User Profile Card */}
      <Card sx={{ mb: 4, background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)', color: 'white' }}>
        <CardContent sx={{ p: 3 }}>
          <Box sx={{ display: 'flex', alignItems: 'center', gap: 3 }}>
            {isLoading ? (
              <Skeleton variant="circular" width={80} height={80} />
            ) : (
              <Avatar
                src={userProfile?.profilePictureUrl}
                sx={{ width: 80, height: 80, border: '3px solid white' }}
              >
                <AccountCircle sx={{ fontSize: 50 }} />
              </Avatar>
            )}
            
            <Box sx={{ flex: 1 }}>
              {isLoading ? (
                <>
                  <Skeleton variant="text" width={200} height={32} />
                  <Skeleton variant="text" width={150} height={24} />
                  <Skeleton variant="text" width={100} height={20} />
                </>
              ) : (
                <>
                  <Typography variant="h5" gutterBottom>
                    {userProfile?.fullName || `${userProfile?.firstName} ${userProfile?.lastName}` || user?.username}
                  </Typography>
                  <Typography variant="body1" sx={{ opacity: 0.9, mb: 1 }}>
                    {userProfile?.email || user?.email}
                  </Typography>
                  <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
                    <Chip
                      label={userProfile?.membershipTier || 'BRONZE'}
                      sx={{
                        bgcolor: getMembershipTierColor(userProfile?.membershipTier || 'BRONZE'),
                        color: 'text.primary',
                        fontWeight: 600,
                      }}
                    />
                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                      <Star sx={{ color: '#FFD700' }} />
                      <Typography variant="body2">
                        {userProfile?.loyaltyPoints || 0} points
                      </Typography>
                    </Box>
                  </Box>
                </>
              )}
            </Box>

            <Button
              variant="outlined"
              sx={{ borderColor: 'white', color: 'white', '&:hover': { borderColor: 'white', bgcolor: 'rgba(255,255,255,0.1)' } }}
              onClick={() => navigate('/profile')}
            >
              Edit Profile
            </Button>
          </Box>
        </CardContent>
      </Card>

      {/* Quick Stats */}
      <Box sx={{ display: 'grid', gridTemplateColumns: { xs: '1fr', sm: 'repeat(2, 1fr)', md: 'repeat(4, 1fr)' }, gap: 3, mb: 4 }}>
        <Card>
          <CardContent sx={{ textAlign: 'center' }}>
            <Receipt sx={{ fontSize: 40, color: 'primary.main', mb: 1 }} />
            <Typography variant="h4" color="primary.main">
              {isLoading ? <Skeleton width={40} /> : userProfile?.totalOrders || 0}
            </Typography>
            <Typography variant="body2" color="text.secondary">
              Total Orders
            </Typography>
          </CardContent>
        </Card>

        <Card>
          <CardContent sx={{ textAlign: 'center' }}>
            <TrendingUp sx={{ fontSize: 40, color: 'success.main', mb: 1 }} />
            <Typography variant="h4" color="success.main">
              {isLoading ? <Skeleton width={60} /> : `$${userProfile?.totalSpent?.toFixed(2) || '0.00'}`}
            </Typography>
            <Typography variant="body2" color="text.secondary">
              Total Spent
            </Typography>
          </CardContent>
        </Card>

        <Card>
          <CardContent sx={{ textAlign: 'center' }}>
            <Star sx={{ fontSize: 40, color: 'warning.main', mb: 1 }} />
            <Typography variant="h4" color="warning.main">
              {isLoading ? <Skeleton width={50} /> : userProfile?.loyaltyPoints || 0}
            </Typography>
            <Typography variant="body2" color="text.secondary">
              Loyalty Points
            </Typography>
          </CardContent>
        </Card>

        <Card>
          <CardContent sx={{ textAlign: 'center' }}>
            <FavoriteOutlined sx={{ fontSize: 40, color: 'error.main', mb: 1 }} />
            <Typography variant="h4" color="error.main">
              {isLoading ? <Skeleton width={30} /> : '0'}
            </Typography>
            <Typography variant="body2" color="text.secondary">
              Wishlist Items
            </Typography>
          </CardContent>
        </Card>
      </Box>

      {/* Quick Actions */}
      <Card sx={{ mb: 4 }}>
        <CardContent>
          <Typography variant="h6" gutterBottom>
            Quick Actions
          </Typography>
          <Box sx={{ display: 'flex', gap: 2, flexWrap: 'wrap' }}>
            <Button
              variant="contained"
              startIcon={<ShoppingCart />}
              onClick={() => navigate('/products')}
            >
              Shop Now
            </Button>
            <Button
              variant="outlined"
              startIcon={<Receipt />}
              onClick={() => navigate('/orders')}
            >
              View Orders
            </Button>
            <Button
              variant="outlined"
              startIcon={<LocalShipping />}
              onClick={() => navigate('/cart')}
            >
              View Cart
            </Button>
            <Button
              variant="outlined"
              startIcon={<AccountCircle />}
              onClick={() => navigate('/profile')}
            >
              Manage Profile
            </Button>
          </Box>
        </CardContent>
      </Card>

      {/* Recent Orders */}
      <Card sx={{ mb: 4 }}>
        <CardContent>
          <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
            <Typography variant="h6">
              Recent Orders
            </Typography>
            <Button
              variant="text"
              onClick={() => navigate('/orders')}
              endIcon={<Add />}
            >
              View All
            </Button>
          </Box>

          {isLoading ? (
            <Box>
              {[1, 2, 3].map((i) => (
                <Box key={i} sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', py: 2, borderBottom: '1px solid', borderColor: 'divider' }}>
                  <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
                    <Skeleton variant="text" width={100} />
                    <Skeleton variant="text" width={150} />
                  </Box>
                  <Skeleton variant="rectangular" width={80} height={24} />
                </Box>
              ))}
            </Box>
          ) : recentOrders.length > 0 ? (
            <Box>
              {recentOrders.map((order) => (
                <Box
                  key={order.id}
                  sx={{
                    display: 'flex',
                    justifyContent: 'space-between',
                    alignItems: 'center',
                    py: 2,
                    borderBottom: '1px solid',
                    borderColor: 'divider',
                    '&:last-child': { borderBottom: 'none' },
                  }}
                >
                  <Box>
                    <Typography variant="body1" fontWeight="medium">
                      Order #{order.orderNumber}
                    </Typography>
                    <Typography variant="body2" color="text.secondary">
                      {new Date(order.createdAt).toLocaleDateString()} • ${order.finalAmount.toFixed(2)}
                    </Typography>
                  </Box>
                  <Chip
                    label={order.status}
                    color={getOrderStatusColor(order.status) as any}
                    size="small"
                  />
                </Box>
              ))}
            </Box>
          ) : (
            <Box sx={{ textAlign: 'center', py: 4 }}>
              <Receipt sx={{ fontSize: 48, color: 'text.disabled', mb: 2 }} />
              <Typography variant="body1" color="text.secondary" gutterBottom>
                No orders yet
              </Typography>
              <Button
                variant="contained"
                onClick={() => navigate('/products')}
                startIcon={<ShoppingCart />}
              >
                Start Shopping
              </Button>
            </Box>
          )}
        </CardContent>
      </Card>

      {/* Featured Products */}
      <Card>
        <CardContent>
          <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
            <Typography variant="h6">
              Featured Products
            </Typography>
            <Button
              variant="text"
              onClick={() => navigate('/products')}
              endIcon={<Add />}
            >
              View All
            </Button>
          </Box>

          {isLoading ? (
            <Box sx={{ display: 'grid', gridTemplateColumns: { xs: '1fr', sm: 'repeat(2, 1fr)', md: 'repeat(4, 1fr)' }, gap: 2 }}>
              {[1, 2, 3, 4].map((i) => (
                <Card key={i}>
                  <Skeleton variant="rectangular" height={150} />
                  <CardContent>
                    <Skeleton variant="text" height={24} />
                    <Skeleton variant="text" height={20} width="60%" />
                  </CardContent>
                </Card>
              ))}
            </Box>
          ) : featuredProducts.length > 0 ? (
            <Box sx={{ display: 'grid', gridTemplateColumns: { xs: '1fr', sm: 'repeat(2, 1fr)', md: 'repeat(4, 1fr)' }, gap: 2 }}>
              {featuredProducts.map((product) => (
                <Card
                  key={product.id}
                  sx={{
                    cursor: 'pointer',
                    transition: 'transform 0.2s ease-in-out',
                    '&:hover': { transform: 'translateY(-4px)' },
                  }}
                  onClick={() => navigate(`/products/${product.id}`)}
                >
                  <Box
                    component="img"
                    src={product.imageUrl || 'https://via.placeholder.com/200x150'}
                    alt={product.name}
                    sx={{
                      width: '100%',
                      height: 150,
                      objectFit: 'cover',
                    }}
                  />
                  <CardContent sx={{ p: 2 }}>
                    <Typography variant="body2" fontWeight="medium" noWrap>
                      {product.name}
                    </Typography>
                    <Typography variant="body1" color="primary.main" fontWeight="bold">
                      ${product.price.toFixed(2)}
                    </Typography>
                  </CardContent>
                </Card>
              ))}
            </Box>
          ) : (
            <Box sx={{ textAlign: 'center', py: 4 }}>
              <Typography variant="body1" color="text.secondary">
                No featured products available
              </Typography>
            </Box>
          )}
        </CardContent>
      </Card>
    </Box>
  );
};

export default UserDashboard; 