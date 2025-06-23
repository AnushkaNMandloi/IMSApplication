import React, { useState, useEffect } from 'react';
import {
  Box,
  Typography,
  Card,
  CardContent,
  CardMedia,
  Button,
  Chip,
  TextField,
  InputAdornment,
  Select,
  MenuItem,
  FormControl,
  InputLabel,
  Pagination,
  Skeleton,
  Alert,
  IconButton,
  Drawer,
  Slider,
  Checkbox,
  FormControlLabel,
  Rating,
  Fab,
} from '@mui/material';
import {
  Search,
  FilterList,
  Sort,
  ShoppingCart,
  Favorite,
  FavoriteBorder,
  Star,
  Close,
  ViewModule,
  ViewList,
} from '@mui/icons-material';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { apiService } from '../../services/api';
import { Product, ProductSearchFilters } from '../../types';
import { toast } from 'react-toastify';

interface ProductFilters {
  categories: string[];
  priceRange: [number, number];
  rating: number;
  inStock: boolean;
}

const ProductsPage: React.FC = () => {
  const navigate = useNavigate();
  const [searchParams, setSearchParams] = useSearchParams();
  
  const [products, setProducts] = useState<Product[]>([]);
  const [filteredProducts, setFilteredProducts] = useState<Product[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [sortBy, setSortBy] = useState('name');
  const [sortOrder, setSortOrder] = useState<'asc' | 'desc'>('asc');
  const [currentPage, setCurrentPage] = useState(1);
  const [itemsPerPage] = useState(12);
  const [viewMode, setViewMode] = useState<'grid' | 'list'>('grid');
  const [filterDrawerOpen, setFilterDrawerOpen] = useState(false);
  const [wishlist, setWishlist] = useState<Set<number>>(new Set());
  
  const [filters, setFilters] = useState<ProductSearchFilters>({
    query: searchParams.get('search') || '',
    category: searchParams.get('category') || '',
    minPrice: searchParams.get('minPrice') ? Number(searchParams.get('minPrice')) : undefined,
    maxPrice: searchParams.get('maxPrice') ? Number(searchParams.get('maxPrice')) : undefined,
    rating: searchParams.get('rating') ? Number(searchParams.get('rating')) : undefined,
    sortBy: (searchParams.get('sortBy') as any) || 'newest',
    sortOrder: (searchParams.get('sortOrder') as any) || 'desc',
  });
  
  const [categories, setCategories] = useState<string[]>([]);

  useEffect(() => {
    loadProducts();
    loadCategories();
    
    // Load initial search from URL params
    const search = searchParams.get('search');
    if (search) {
      setSearchTerm(search);
    }
  }, [searchParams]);

  useEffect(() => {
    applyFiltersAndSort();
  }, [products, searchTerm, sortBy, sortOrder, filters]);

  const loadProducts = async () => {
    try {
      setIsLoading(true);
      setError(null);
      const productsData = await apiService.getProducts(filters);
      setProducts(productsData.content || []);
      setFilteredProducts(productsData.content || []);
    } catch (error: any) {
      console.error('Failed to load products:', error);
      setError('Failed to load products. Please try again.');
    } finally {
      setIsLoading(false);
    }
  };

  const loadCategories = async () => {
    try {
      const categoriesData = await apiService.getProductsByCategory('');
      const uniqueCategories = [...new Set(categoriesData.map(product => product.category))];
      setCategories(uniqueCategories);
    } catch (error) {
      console.error('Failed to load categories:', error);
    }
  };

  const applyFiltersAndSort = () => {
    let filtered = [...products];

    // Apply search filter
    if (searchTerm) {
      filtered = filtered.filter(product =>
        product.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
        product.description?.toLowerCase().includes(searchTerm.toLowerCase()) ||
        product.category?.toLowerCase().includes(searchTerm.toLowerCase())
      );
    }

    // Apply category filter
    if (filters.category) {
      filtered = filtered.filter(product =>
        product.category && product.category.toLowerCase().includes(filters.category.toLowerCase())
      );
    }

    // Apply price range filter
    if (filters.minPrice !== undefined && filters.maxPrice !== undefined) {
      filtered = filtered.filter(product =>
        product.price >= filters.minPrice && product.price <= filters.maxPrice
      );
    }

    // Apply rating filter
    if (filters.rating !== undefined) {
      filtered = filtered.filter(product =>
        (product.rating || 0) >= filters.rating
      );
    }

    // Apply stock filter
    if (filters.inStock) {
      filtered = filtered.filter(product => product.stock > 0);
    }

    // Apply sorting
    filtered.sort((a, b) => {
      let comparison = 0;
      
      switch (sortBy) {
        case 'name':
          comparison = a.name.localeCompare(b.name);
          break;
        case 'price':
          comparison = a.price - b.price;
          break;
        case 'rating':
          comparison = (b.rating || 0) - (a.rating || 0);
          break;
        case 'newest':
          comparison = new Date(b.createdAt || '').getTime() - new Date(a.createdAt || '').getTime();
          break;
        default:
          comparison = 0;
      }
      
      return sortOrder === 'desc' ? -comparison : comparison;
    });

    setFilteredProducts(filtered);
    setCurrentPage(1); // Reset to first page when filters change
  };

  const handleSearch = (event: React.ChangeEvent<HTMLInputElement>) => {
    const value = event.target.value;
    setSearchTerm(value);
    
    // Update URL params
    const newSearchParams = new URLSearchParams(searchParams);
    if (value) {
      newSearchParams.set('search', value);
    } else {
      newSearchParams.delete('search');
    }
    setSearchParams(newSearchParams);
  };

  const handleAddToCart = async (productId: number) => {
    try {
      await apiService.addToCart(productId, 1);
      toast.success('Product added to cart!');
    } catch (error: any) {
      toast.error('Failed to add product to cart. Please try again.');
    }
  };

  const handleToggleWishlist = async (productId: number) => {
    try {
      const newWishlist = new Set(wishlist);
      if (wishlist.has(productId)) {
        newWishlist.delete(productId);
        toast.success('Removed from wishlist');
      } else {
        newWishlist.add(productId);
        toast.success('Added to wishlist');
      }
      setWishlist(newWishlist);
    } catch (error: any) {
      toast.error('Failed to update wishlist');
    }
  };

  const handleCategoryFilter = (category: string) => {
    const newCategories = filters.categories.includes(category)
      ? filters.categories.filter(c => c !== category)
      : [...filters.categories, category];
    
    setFilters({ ...filters, categories: newCategories });
  };

  const clearFilters = () => {
    setFilters({
      query: '',
      category: '',
      minPrice: undefined,
      maxPrice: undefined,
      rating: undefined,
      sortBy: 'newest',
      sortOrder: 'desc',
    });
    setSearchTerm('');
    setSearchParams(new URLSearchParams());
  };

  // Pagination
  const totalPages = Math.ceil(filteredProducts.length / itemsPerPage);
  const startIndex = (currentPage - 1) * itemsPerPage;
  const currentProducts = filteredProducts.slice(startIndex, startIndex + itemsPerPage);

  const ProductCard = ({ product }: { product: Product }) => (
    <Card
      sx={{
        height: '100%',
        display: 'flex',
        flexDirection: 'column',
        transition: 'transform 0.2s ease-in-out, box-shadow 0.2s ease-in-out',
        '&:hover': {
          transform: 'translateY(-4px)',
          boxShadow: 4,
        },
        cursor: 'pointer',
      }}
      onClick={() => navigate(`/products/${product.id}`)}
    >
      <Box sx={{ position: 'relative' }}>
        <CardMedia
          component="img"
          height="200"
          image={product.imageUrl || 'https://via.placeholder.com/300x200'}
          alt={product.name}
          sx={{ objectFit: 'cover' }}
        />
        
        {/* Wishlist Button */}
        <IconButton
          onClick={(e) => {
            e.stopPropagation();
            handleToggleWishlist(product.id);
          }}
          sx={{
            position: 'absolute',
            top: 8,
            right: 8,
            bgcolor: 'white',
            '&:hover': { bgcolor: 'grey.100' },
          }}
        >
          {wishlist.has(product.id) ? (
            <Favorite color="error" />
          ) : (
            <FavoriteBorder />
          )}
        </IconButton>

        {/* Stock Status */}
        {product.stock === 0 && (
          <Chip
            label="Out of Stock"
            color="error"
            size="small"
            sx={{
              position: 'absolute',
              top: 8,
              left: 8,
            }}
          />
        )}
      </Box>

      <CardContent sx={{ flexGrow: 1, display: 'flex', flexDirection: 'column' }}>
        <Typography variant="h6" component="h3" gutterBottom noWrap>
          {product.name}
        </Typography>
        
        <Typography
          variant="body2"
          color="text.secondary"
          sx={{
            flexGrow: 1,
            overflow: 'hidden',
            textOverflow: 'ellipsis',
            display: '-webkit-box',
            WebkitLineClamp: 2,
            WebkitBoxOrient: 'vertical',
            mb: 2,
          }}
        >
          {product.description}
        </Typography>

        {/* Rating */}
        <Box sx={{ display: 'flex', alignItems: 'center', mb: 1 }}>
          <Rating
            value={product.rating || 0}
            precision={0.5}
            size="small"
            readOnly
          />
          <Typography variant="body2" color="text.secondary" sx={{ ml: 1 }}>
            ({product.reviewCount || 0})
          </Typography>
        </Box>

        {/* Price and Category */}
        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
          <Typography variant="h6" color="primary.main" fontWeight="bold">
            ${product.price.toFixed(2)}
          </Typography>
          {product.category && (
            <Chip label={product.category} size="small" variant="outlined" />
          )}
        </Box>

        {/* Add to Cart Button */}
        <Button
          variant="contained"
          startIcon={<ShoppingCart />}
          fullWidth
          disabled={product.stock === 0}
          onClick={(e) => {
            e.stopPropagation();
            handleAddToCart(product.id);
          }}
          sx={{ mt: 'auto' }}
        >
          {product.stock === 0 ? 'Out of Stock' : 'Add to Cart'}
        </Button>
      </CardContent>
    </Card>
  );

  const FilterDrawer = () => (
    <Drawer
      anchor="left"
      open={filterDrawerOpen}
      onClose={() => setFilterDrawerOpen(false)}
    >
      <Box sx={{ width: 300, p: 3 }}>
        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
          <Typography variant="h6">Filters</Typography>
          <IconButton onClick={() => setFilterDrawerOpen(false)}>
            <Close />
          </IconButton>
        </Box>

        {/* Categories */}
        <Typography variant="subtitle1" gutterBottom>
          Categories
        </Typography>
        <Box sx={{ mb: 3 }}>
          {categories.map((category) => (
            <FormControlLabel
              key={category}
              control={
                <Checkbox
                  checked={filters.categories.includes(category)}
                  onChange={() => handleCategoryFilter(category)}
                />
              }
              label={category}
            />
          ))}
        </Box>

        {/* Price Range */}
        <Typography variant="subtitle1" gutterBottom>
          Price Range: ${filters.minPrice} - ${filters.maxPrice}
        </Typography>
        <Slider
          value={[filters.minPrice, filters.maxPrice]}
          onChange={(_, newValue) =>
            setFilters({ ...filters, minPrice: newValue[0], maxPrice: newValue[1] })
          }
          valueLabelDisplay="auto"
          min={0}
          max={1000}
          sx={{ mb: 3 }}
        />

        {/* Rating */}
        <Typography variant="subtitle1" gutterBottom>
          Minimum Rating
        </Typography>
        <Rating
          value={filters.rating}
          onChange={(_, newValue) =>
            setFilters({ ...filters, rating: newValue || 0 })
          }
          sx={{ mb: 3 }}
        />

        {/* In Stock Only */}
        <FormControlLabel
          control={
            <Checkbox
              checked={filters.inStock}
              onChange={(e) =>
                setFilters({ ...filters, inStock: e.target.checked })
              }
            />
          }
          label="In Stock Only"
          sx={{ mb: 3 }}
        />

        {/* Clear Filters */}
        <Button
          variant="outlined"
          fullWidth
          onClick={clearFilters}
        >
          Clear All Filters
        </Button>
      </Box>
    </Drawer>
  );

  if (error) {
    return (
      <Box sx={{ p: 3 }}>
        <Alert severity="error" action={
          <Button color="inherit" size="small" onClick={loadProducts}>
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
      <Typography variant="h4" component="h1" gutterBottom>
        Products
      </Typography>

      {/* Search and Controls */}
      <Box sx={{ display: 'flex', gap: 2, mb: 3, flexWrap: 'wrap' }}>
        <TextField
          placeholder="Search products..."
          value={searchTerm}
          onChange={handleSearch}
          InputProps={{
            startAdornment: (
              <InputAdornment position="start">
                <Search />
              </InputAdornment>
            ),
          }}
          sx={{ flexGrow: 1, minWidth: 300 }}
        />

        <Button
          variant="outlined"
          startIcon={<FilterList />}
          onClick={() => setFilterDrawerOpen(true)}
        >
          Filters
        </Button>

        <FormControl sx={{ minWidth: 150 }}>
          <InputLabel>Sort By</InputLabel>
          <Select
            value={sortBy}
            label="Sort By"
            onChange={(e) => setSortBy(e.target.value)}
          >
            <MenuItem value="name">Name</MenuItem>
            <MenuItem value="price">Price</MenuItem>
            <MenuItem value="rating">Rating</MenuItem>
            <MenuItem value="newest">Newest</MenuItem>
          </Select>
        </FormControl>

        <FormControl sx={{ minWidth: 100 }}>
          <InputLabel>Order</InputLabel>
          <Select
            value={sortOrder}
            label="Order"
            onChange={(e) => setSortOrder(e.target.value as 'asc' | 'desc')}
          >
            <MenuItem value="asc">Asc</MenuItem>
            <MenuItem value="desc">Desc</MenuItem>
          </Select>
        </FormControl>

        <Box>
          <IconButton
            onClick={() => setViewMode('grid')}
            color={viewMode === 'grid' ? 'primary' : 'default'}
          >
            <ViewModule />
          </IconButton>
          <IconButton
            onClick={() => setViewMode('list')}
            color={viewMode === 'list' ? 'primary' : 'default'}
          >
            <ViewList />
          </IconButton>
        </Box>
      </Box>

      {/* Results Info */}
      <Typography variant="body2" color="text.secondary" sx={{ mb: 3 }}>
        Showing {startIndex + 1}-{Math.min(startIndex + itemsPerPage, filteredProducts.length)} of {filteredProducts.length} products
      </Typography>

      {/* Products Grid/List */}
      {isLoading ? (
        <Box sx={{ display: 'grid', gridTemplateColumns: { xs: '1fr', sm: 'repeat(2, 1fr)', md: 'repeat(3, 1fr)', lg: 'repeat(4, 1fr)' }, gap: 3 }}>
          {Array.from({ length: 8 }).map((_, index) => (
            <Card key={index}>
              <Skeleton variant="rectangular" height={200} />
              <CardContent>
                <Skeleton variant="text" height={24} />
                <Skeleton variant="text" height={20} width="60%" />
                <Skeleton variant="text" height={32} width="40%" />
              </CardContent>
            </Card>
          ))}
        </Box>
      ) : currentProducts.length > 0 ? (
        <Box
          sx={{
            display: 'grid',
            gridTemplateColumns: viewMode === 'grid'
              ? { xs: '1fr', sm: 'repeat(2, 1fr)', md: 'repeat(3, 1fr)', lg: 'repeat(4, 1fr)' }
              : '1fr',
            gap: 3,
            mb: 4,
          }}
        >
          {currentProducts.map((product) => (
            <ProductCard key={product.id} product={product} />
          ))}
        </Box>
      ) : (
        <Box sx={{ textAlign: 'center', py: 8 }}>
          <Typography variant="h6" gutterBottom>
            No products found
          </Typography>
          <Typography variant="body2" color="text.secondary" gutterBottom>
            Try adjusting your search or filters
          </Typography>
          <Button variant="contained" onClick={clearFilters}>
            Clear Filters
          </Button>
        </Box>
      )}

      {/* Pagination */}
      {totalPages > 1 && (
        <Box sx={{ display: 'flex', justifyContent: 'center', mt: 4 }}>
          <Pagination
            count={totalPages}
            page={currentPage}
            onChange={(_, page) => setCurrentPage(page)}
            color="primary"
            size="large"
          />
        </Box>
      )}

      {/* Filter Drawer */}
      <FilterDrawer />

      {/* Floating Action Button for Mobile Filter */}
      <Fab
        color="primary"
        sx={{
          position: 'fixed',
          bottom: 16,
          right: 16,
          display: { xs: 'flex', md: 'none' },
        }}
        onClick={() => setFilterDrawerOpen(true)}
      >
        <FilterList />
      </Fab>
    </Box>
  );
};

export default ProductsPage; 