import React, { useState, useEffect } from 'react';
import {
  Box,
  Typography,
  Button,
  Chip,
  Rating,
  Divider,
  Card,
  CardContent,
  TextField,
  Avatar,
  Alert,
  Skeleton,
  Tabs,
  Tab,
  IconButton,
  Badge,
  Breadcrumbs,
  Link,
} from '@mui/material';
import {
  ShoppingCart,
  Favorite,
  FavoriteBorder,
  Share,
  Add,
  Remove,
  Star,
  Verified,
  ArrowBack,
  NavigateNext,
} from '@mui/icons-material';
import { useParams, useNavigate, Link as RouterLink } from 'react-router-dom';
import { apiService } from '../../services/api';
import { Product, ProductReview } from '../../types';
import { toast } from 'react-toastify';
import { useAuth } from '../../contexts/AuthContext';

interface TabPanelProps {
  children?: React.ReactNode;
  index: number;
  value: number;
}

const TabPanel = ({ children, value, index }: TabPanelProps) => (
  <div role="tabpanel" hidden={value !== index}>
    {value === index && <Box sx={{ p: 3 }}>{children}</Box>}
  </div>
);

const ProductDetailPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const [product, setProduct] = useState<Product | null>(null);
  const [reviews, setReviews] = useState<any[]>([]);
  const [relatedProducts, setRelatedProducts] = useState<Product[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [quantity, setQuantity] = useState(1);
  const [selectedImageIndex, setSelectedImageIndex] = useState(0);
  const [tabValue, setTabValue] = useState(0);
  const [isInWishlist, setIsInWishlist] = useState(false);
  const [newReview, setNewReview] = useState({ rating: 0, comment: '' });
  const { user } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    if (id) {
      loadProductDetails(parseInt(id));
    }
  }, [id]);

  const loadProductDetails = async (productId: number) => {
    try {
      setIsLoading(true);
      setError(null);

      const [productData, reviewsData, relatedData] = await Promise.all([
        apiService.getProduct(productId),
        apiService.getProductReviews(productId).catch(() => []),
        apiService.getFeaturedProducts().catch(() => []),
      ]);

      setProduct(productData);
      setReviews(reviewsData);
      setRelatedProducts(relatedData.slice(0, 4));
    } catch (error: any) {
      console.error('Failed to load product details:', error);
      setError('Failed to load product details. Please try again.');
    } finally {
      setIsLoading(false);
    }
  };

  const handleAddToCart = async () => {
    if (!product) return;
    
    try {
      await apiService.addToCart(product.id, quantity);
      toast.success(`Added ${quantity} ${product.name}(s) to cart!`);
    } catch (error: any) {
      toast.error('Failed to add product to cart. Please try again.');
    }
  };

  const handleToggleWishlist = async () => {
    if (!product) return;
    
    try {
      if (isInWishlist) {
        // Remove from wishlist logic
        setIsInWishlist(false);
        toast.success('Removed from wishlist');
      } else {
        // Add to wishlist logic
        setIsInWishlist(true);
        toast.success('Added to wishlist');
      }
    } catch (error: any) {
      toast.error('Failed to update wishlist');
    }
  };

  const handleShare = async () => {
    if (navigator.share && product) {
      try {
        await navigator.share({
          title: product.name,
          text: product.description,
          url: window.location.href,
        });
      } catch (error) {
        // Fallback to copying URL
        navigator.clipboard.writeText(window.location.href);
        toast.success('Product link copied to clipboard!');
      }
    } else {
      navigator.clipboard.writeText(window.location.href);
      toast.success('Product link copied to clipboard!');
    }
  };

  const handleSubmitReview = async () => {
    if (!product || !user) {
      toast.error('Please log in to submit a review');
      return;
    }

    if (newReview.rating === 0) {
      toast.error('Please select a rating');
      return;
    }

    try {
      await apiService.addProductReview(product.id, {
        rating: newReview.rating,
        reviewText: newReview.comment,
      });
      
      toast.success('Review submitted successfully!');
      setNewReview({ rating: 0, comment: '' });
      
      // Reload reviews
      const updatedReviews = await apiService.getProductReviews(product.id);
      setReviews(updatedReviews);
    } catch (error: any) {
      toast.error('Failed to submit review. Please try again.');
    }
  };

  const getStockStatus = (stock: number) => {
    if (stock === 0) return { label: 'Out of Stock', color: 'error' as const };
    if (stock < 10) return { label: 'Low Stock', color: 'warning' as const };
    return { label: 'In Stock', color: 'success' as const };
  };

  if (error) {
    return (
      <Box sx={{ p: 3 }}>
        <Alert severity="error" action={
          <Button color="inherit" size="small" onClick={() => loadProductDetails(parseInt(id!))}>
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
        <Skeleton variant="text" width={300} height={40} sx={{ mb: 2 }} />
        <Box sx={{ display: 'grid', gridTemplateColumns: { xs: '1fr', md: '1fr 1fr' }, gap: 4 }}>
          <Skeleton variant="rectangular" height={400} />
          <Box>
            <Skeleton variant="text" height={40} sx={{ mb: 2 }} />
            <Skeleton variant="text" height={30} sx={{ mb: 2 }} />
            <Skeleton variant="text" height={100} sx={{ mb: 2 }} />
            <Skeleton variant="rectangular" height={50} />
          </Box>
        </Box>
      </Box>
    );
  }

  if (!product) {
    return (
      <Box sx={{ p: 3, textAlign: 'center' }}>
        <Typography variant="h5" gutterBottom>
          Product not found
        </Typography>
        <Button variant="contained" onClick={() => navigate('/products')}>
          Back to Products
        </Button>
      </Box>
    );
  }

  const stockStatus = getStockStatus(product.stock);
  const images = product.imageUrls.length > 0 ? product.imageUrls : product.imageUrl ? [product.imageUrl] : ['https://via.placeholder.com/400x400'];

  return (
    <Box sx={{ p: 3 }}>
      {/* Breadcrumbs */}
      <Breadcrumbs separator={<NavigateNext fontSize="small" />} sx={{ mb: 3 }}>
        <Link component={RouterLink} to="/" underline="hover">
          Home
        </Link>
        <Link component={RouterLink} to="/products" underline="hover">
          Products
        </Link>
        {product.category && (
          <Link component={RouterLink} to={`/products?category=${product.category}`} underline="hover">
            {product.category}
          </Link>
        )}
        <Typography color="text.primary">{product.name}</Typography>
      </Breadcrumbs>

      {/* Back Button */}
      <Button
        startIcon={<ArrowBack />}
        onClick={() => navigate(-1)}
        sx={{ mb: 3 }}
      >
        Back
      </Button>

      {/* Product Details */}
      <Box sx={{ display: 'grid', gridTemplateColumns: { xs: '1fr', md: '1fr 1fr' }, gap: 4, mb: 4 }}>
        {/* Product Images */}
        <Box>
          <Box
            component="img"
            src={images[selectedImageIndex]}
            alt={product.name}
            sx={{
              width: '100%',
              height: 400,
              objectFit: 'cover',
              borderRadius: 2,
              mb: 2,
            }}
          />
          
          {/* Thumbnail Images */}
          {images.length > 1 && (
            <Box sx={{ display: 'flex', gap: 1, overflowX: 'auto' }}>
              {images.map((image, index) => (
                <Box
                  key={index}
                  component="img"
                  src={image}
                  alt={`${product.name} ${index + 1}`}
                  onClick={() => setSelectedImageIndex(index)}
                  sx={{
                    width: 80,
                    height: 80,
                    objectFit: 'cover',
                    borderRadius: 1,
                    cursor: 'pointer',
                    border: selectedImageIndex === index ? '2px solid' : '1px solid',
                    borderColor: selectedImageIndex === index ? 'primary.main' : 'grey.300',
                  }}
                />
              ))}
            </Box>
          )}
        </Box>

        {/* Product Info */}
        <Box>
          <Typography variant="h4" component="h1" gutterBottom>
            {product.name}
          </Typography>

          {/* Rating and Reviews */}
          <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, mb: 2 }}>
            <Rating value={product.rating || 0} precision={0.5} readOnly />
            <Typography variant="body2" color="text.secondary">
              ({product.reviewCount || 0} reviews)
            </Typography>
          </Box>

          {/* Price */}
          <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, mb: 2 }}>
            <Typography variant="h3" color="primary.main" fontWeight="bold">
              ${product.price.toFixed(2)}
            </Typography>
            {product.originalPrice && product.originalPrice > product.price && (
              <>
                <Typography variant="h5" sx={{ textDecoration: 'line-through', color: 'text.secondary' }}>
                  ${product.originalPrice.toFixed(2)}
                </Typography>
                {product.discountPercentage && (
                  <Chip
                    label={`${product.discountPercentage}% OFF`}
                    color="error"
                    size="small"
                  />
                )}
              </>
            )}
          </Box>

          {/* Stock Status */}
          <Chip
            label={stockStatus.label}
            color={stockStatus.color}
            sx={{ mb: 2 }}
          />

          {/* Category and Seller */}
          <Box sx={{ display: 'flex', gap: 2, mb: 3, flexWrap: 'wrap' }}>
            {product.category && (
              <Chip label={product.category} variant="outlined" />
            )}
            {product.subcategory && (
              <Chip label={product.subcategory} variant="outlined" size="small" />
            )}
            {product.brand && (
              <Chip label={product.brand} variant="outlined" size="small" />
            )}
            <Chip
              label={`Sold by ${product.sellerName}`}
              variant="outlined"
              icon={<Verified />}
            />
          </Box>

          {/* Product Attributes */}
          {(product.color || product.size || product.material) && (
            <Box sx={{ display: 'flex', gap: 2, mb: 3, flexWrap: 'wrap' }}>
              {product.color && (
                <Box>
                  <Typography variant="body2" color="text.secondary">Color:</Typography>
                  <Typography variant="body1">{product.color}</Typography>
                </Box>
              )}
              {product.size && (
                <Box>
                  <Typography variant="body2" color="text.secondary">Size:</Typography>
                  <Typography variant="body1">{product.size}</Typography>
                </Box>
              )}
              {product.material && (
                <Box>
                  <Typography variant="body2" color="text.secondary">Material:</Typography>
                  <Typography variant="body1">{product.material}</Typography>
                </Box>
              )}
            </Box>
          )}

          {/* Description */}
          <Typography variant="body1" sx={{ mb: 3 }}>
            {product.description}
          </Typography>

          {/* Quantity Selector */}
          <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, mb: 3 }}>
            <Typography variant="body1">Quantity:</Typography>
            <Box sx={{ display: 'flex', alignItems: 'center', border: '1px solid', borderColor: 'grey.300', borderRadius: 1 }}>
              <IconButton
                onClick={() => setQuantity(Math.max(product.minOrderQuantity, quantity - 1))}
                disabled={quantity <= product.minOrderQuantity}
              >
                <Remove />
              </IconButton>
              <Typography sx={{ px: 2, minWidth: 40, textAlign: 'center' }}>
                {quantity}
              </Typography>
              <IconButton
                onClick={() => setQuantity(Math.min(product.maxOrderQuantity || product.stock, quantity + 1))}
                disabled={quantity >= (product.maxOrderQuantity || product.stock)}
              >
                <Add />
              </IconButton>
            </Box>
            <Typography variant="body2" color="text.secondary">
              {product.stock} available
            </Typography>
          </Box>

          {/* Action Buttons */}
          <Box sx={{ display: 'flex', gap: 2, mb: 3 }}>
            <Button
              variant="contained"
              startIcon={<ShoppingCart />}
              onClick={handleAddToCart}
              disabled={product.stock === 0}
              size="large"
              sx={{ flexGrow: 1 }}
            >
              Add to Cart
            </Button>
            
            <IconButton
              onClick={handleToggleWishlist}
              color={isInWishlist ? 'error' : 'default'}
              size="large"
            >
              {isInWishlist ? <Favorite /> : <FavoriteBorder />}
            </IconButton>
            
            <IconButton onClick={handleShare} size="large">
              <Share />
            </IconButton>
          </Box>
        </Box>
      </Box>

      {/* Tabs Section */}
      <Card>
        <Tabs value={tabValue} onChange={(_, newValue) => setTabValue(newValue)}>
          <Tab label="Description" />
          <Tab label={`Reviews (${reviews.length})`} />
          <Tab label="Specifications" />
        </Tabs>

        <TabPanel value={tabValue} index={0}>
          <Typography variant="body1" sx={{ mb: 2 }}>
            {product.description || 'No detailed description available.'}
          </Typography>
          
          {/* Product Tags */}
          {product.tags && product.tags.length > 0 && (
            <Box>
              <Typography variant="h6" gutterBottom>Tags</Typography>
              <Box sx={{ display: 'flex', gap: 1, flexWrap: 'wrap' }}>
                {product.tags.map((tag, index) => (
                  <Chip key={index} label={tag} size="small" variant="outlined" />
                ))}
              </Box>
            </Box>
          )}
        </TabPanel>

        <TabPanel value={tabValue} index={1}>
          {/* Reviews Section */}
          <Box>
            {/* Add Review Form */}
            {user && (
              <Card sx={{ mb: 3, p: 3 }}>
                <Typography variant="h6" gutterBottom>
                  Write a Review
                </Typography>
                <Box sx={{ mb: 2 }}>
                  <Typography variant="body2" gutterBottom>
                    Rating:
                  </Typography>
                  <Rating
                    value={newReview.rating}
                    onChange={(_, newValue) =>
                      setNewReview({ ...newReview, rating: newValue || 0 })
                    }
                  />
                </Box>
                <TextField
                  fullWidth
                  multiline
                  rows={3}
                  placeholder="Share your experience with this product..."
                  value={newReview.comment}
                  onChange={(e) =>
                    setNewReview({ ...newReview, comment: e.target.value })
                  }
                  sx={{ mb: 2 }}
                />
                <Button
                  variant="contained"
                  onClick={handleSubmitReview}
                  disabled={newReview.rating === 0}
                >
                  Submit Review
                </Button>
              </Card>
            )}

            {/* Reviews List */}
            {reviews.length > 0 ? (
              <Box>
                {reviews.map((review) => (
                  <Card key={review.id} sx={{ mb: 2, p: 3 }}>
                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, mb: 2 }}>
                      <Avatar>{review.username?.[0]?.toUpperCase()}</Avatar>
                      <Box>
                        <Typography variant="subtitle1">
                          {review.username}
                        </Typography>
                        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                          <Rating value={review.rating} size="small" readOnly />
                          <Typography variant="body2" color="text.secondary">
                            {new Date(review.createdAt).toLocaleDateString()}
                          </Typography>
                          {review.verifiedPurchase && (
                            <Chip label="Verified Purchase" size="small" color="success" />
                          )}
                        </Box>
                      </Box>
                    </Box>
                    {review.title && (
                      <Typography variant="subtitle2" fontWeight="bold" sx={{ mb: 1 }}>
                        {review.title}
                      </Typography>
                    )}
                    <Typography variant="body1">
                      {review.reviewText}
                    </Typography>
                  </Card>
                ))}
              </Box>
            ) : (
              <Typography variant="body1" color="text.secondary" textAlign="center">
                No reviews yet. Be the first to review this product!
              </Typography>
            )}
          </Box>
        </TabPanel>

        <TabPanel value={tabValue} index={2}>
          <Box>
            <Typography variant="h6" gutterBottom>Product Specifications</Typography>
            
            <Box sx={{ display: 'grid', gridTemplateColumns: { xs: '1fr', md: 'repeat(2, 1fr)' }, gap: 2 }}>
              <Box>
                <Typography variant="body2" color="text.secondary">SKU:</Typography>
                <Typography variant="body1">{product.sku}</Typography>
              </Box>
              
              {product.brand && (
                <Box>
                  <Typography variant="body2" color="text.secondary">Brand:</Typography>
                  <Typography variant="body1">{product.brand}</Typography>
                </Box>
              )}
              
              {product.weight && (
                <Box>
                  <Typography variant="body2" color="text.secondary">Weight:</Typography>
                  <Typography variant="body1">{product.weight} kg</Typography>
                </Box>
              )}
              
              {product.dimensions && (
                <Box>
                  <Typography variant="body2" color="text.secondary">Dimensions:</Typography>
                  <Typography variant="body1">{product.dimensions}</Typography>
                </Box>
              )}
              
              <Box>
                <Typography variant="body2" color="text.secondary">Min Order Quantity:</Typography>
                <Typography variant="body1">{product.minOrderQuantity}</Typography>
              </Box>
              
              {product.maxOrderQuantity && (
                <Box>
                  <Typography variant="body2" color="text.secondary">Max Order Quantity:</Typography>
                  <Typography variant="body1">{product.maxOrderQuantity}</Typography>
                </Box>
              )}
            </Box>
          </Box>
        </TabPanel>
      </Card>

      {/* Related Products */}
      {relatedProducts.length > 0 && (
        <Box sx={{ mt: 4 }}>
          <Typography variant="h5" gutterBottom>
            Related Products
          </Typography>
          <Box sx={{ display: 'grid', gridTemplateColumns: { xs: '1fr', sm: 'repeat(2, 1fr)', md: 'repeat(4, 1fr)' }, gap: 2 }}>
            {relatedProducts.map((relatedProduct) => (
              <Card
                key={relatedProduct.id}
                sx={{
                  cursor: 'pointer',
                  transition: 'transform 0.2s ease-in-out',
                  '&:hover': { transform: 'translateY(-4px)' },
                }}
                onClick={() => navigate(`/products/${relatedProduct.id}`)}
              >
                <Box
                  component="img"
                  src={relatedProduct.imageUrl || 'https://via.placeholder.com/200x150'}
                  alt={relatedProduct.name}
                  sx={{
                    width: '100%',
                    height: 150,
                    objectFit: 'cover',
                  }}
                />
                <CardContent sx={{ p: 2 }}>
                  <Typography variant="body2" fontWeight="medium" noWrap>
                    {relatedProduct.name}
                  </Typography>
                  <Typography variant="body1" color="primary.main" fontWeight="bold">
                    ${relatedProduct.price.toFixed(2)}
                  </Typography>
                </CardContent>
              </Card>
            ))}
          </Box>
        </Box>
      )}
    </Box>
  );
};

export default ProductDetailPage; 