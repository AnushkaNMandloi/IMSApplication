// User types
export interface User {
  id: number;
  username: string;
  email: string;
  role: 'USER' | 'SELLER' | 'ADMIN';
  isActive: boolean;
  emailVerified: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface UserProfile {
  id: number;
  userId: number;
  username: string;
  email: string;
  firstName?: string;
  lastName?: string;
  fullName?: string;
  profilePictureUrl?: string;
  dateOfBirth?: string;
  gender?: 'MALE' | 'FEMALE' | 'OTHER' | 'PREFER_NOT_TO_SAY';
  phoneNumber?: string;
  phoneVerified: boolean;
  alternateEmail?: string;
  alternateEmailVerified: boolean;
  bio?: string;
  websiteUrl?: string;
  occupation?: string;
  company?: string;
  facebookUrl?: string;
  twitterUrl?: string;
  linkedinUrl?: string;
  instagramUrl?: string;
  profileVisibility: 'PUBLIC' | 'FRIENDS_ONLY' | 'PRIVATE';
  emailNotifications: boolean;
  smsNotifications: boolean;
  marketingEmails: boolean;
  totalOrders: number;
  totalSpent: number;
  loyaltyPoints: number;
  membershipTier: 'BRONZE' | 'SILVER' | 'GOLD' | 'PLATINUM' | 'DIAMOND';
  lastLoginAt?: string;
  loginCount: number;
  addresses: UserAddress[];
  createdAt: string;
  updatedAt: string;
}

export interface UserAddress {
  id: number;
  addressType: 'HOME' | 'OFFICE' | 'BILLING' | 'SHIPPING' | 'OTHER';
  label?: string;
  recipientName: string;
  phoneNumber?: string;
  addressLine1: string;
  addressLine2?: string;
  landmark?: string;
  city: string;
  state: string;
  postalCode: string;
  country: string;
  latitude?: number;
  longitude?: number;
  isDefault: boolean;
  isActive: boolean;
  deliveryInstructions?: string;
  fullAddress: string;
  usageCount: number;
  lastUsedAt?: string;
  createdAt: string;
  updatedAt: string;
}

// Product types
export interface Product {
  id: number;
  name: string;
  description: string;
  category: string;
  subcategory?: string;
  price: number;
  originalPrice?: number;
  discountPercentage?: number;
  stock: number;
  minOrderQuantity: number;
  maxOrderQuantity?: number;
  sku: string;
  brand?: string;
  color?: string;
  size?: string;
  weight?: number;
  dimensions?: string;
  material?: string;
  imageUrl?: string;
  imageUrls: string[];
  sellerName: string;
  sellerId: number;
  rating: number;
  reviewCount: number;
  isActive: boolean;
  isFeatured: boolean;
  tags: string[];
  attributes: Record<string, any>;
  createdAt: string;
  updatedAt: string;
}

export interface ProductReview {
  id: number;
  userId: number;
  username: string;
  orderId?: number;
  rating: number;
  title?: string;
  reviewText?: string;
  imageUrls: string[];
  verifiedPurchase: boolean;
  helpfulCount: number;
  notHelpfulCount: number;
  status: 'PENDING' | 'APPROVED' | 'REJECTED' | 'HIDDEN' | 'DELETED';
  sellerResponse?: string;
  sellerResponseDate?: string;
  isFlagged: boolean;
  createdAt: string;
  updatedAt: string;
}

// Cart types
export interface CartItem {
  id: number;
  productId: number;
  productName: string;
  productImage?: string;
  price: number;
  quantity: number;
  maxQuantity: number;
  sellerName: string;
  sellerId: number;
  subtotal: number;
  isAvailable: boolean;
  addedAt: string;
  updatedAt: string;
}

export interface Cart {
  id: string;
  userId?: number;
  items: CartItem[];
  totalItems: number;
  totalAmount: number;
  discountAmount: number;
  finalAmount: number;
  isExpired: boolean;
  expiresAt: string;
  createdAt: string;
  updatedAt: string;
}

// Order types
export interface Order {
  id: number;
  orderNumber: string;
  userId: number;
  customerName: string;
  customerEmail: string;
  status: 'PENDING' | 'CONFIRMED' | 'PROCESSING' | 'SHIPPED' | 'DELIVERED' | 'CANCELLED' | 'RETURNED';
  paymentStatus: 'PENDING' | 'PAID' | 'FAILED' | 'REFUNDED' | 'PARTIALLY_REFUNDED';
  paymentMethod: 'CREDIT_CARD' | 'DEBIT_CARD' | 'UPI' | 'NET_BANKING' | 'WALLET' | 'COD';
  totalAmount: number;
  discountAmount: number;
  shippingAmount: number;
  taxAmount: number;
  finalAmount: number;
  items: OrderItem[];
  shippingAddress: ShippingAddress;
  billingAddress?: ShippingAddress;
  trackingNumber?: string;
  estimatedDeliveryDate?: string;
  actualDeliveryDate?: string;
  cancellationReason?: string;
  returnReason?: string;
  refundAmount?: number;
  statusHistory: OrderStatusHistory[];
  createdAt: string;
  updatedAt: string;
}

export interface OrderItem {
  id: number;
  productId: number;
  productName: string;
  productImage?: string;
  sellerId: number;
  sellerName: string;
  price: number;
  quantity: number;
  subtotal: number;
  status: 'PENDING' | 'CONFIRMED' | 'SHIPPED' | 'DELIVERED' | 'CANCELLED' | 'RETURNED';
}

export interface ShippingAddress {
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

export interface OrderStatusHistory {
  id: number;
  status: string;
  notes?: string;
  timestamp: string;
  updatedBy?: string;
}

// Seller types
export interface Seller {
  id: number;
  businessName: string;
  ownerName: string;
  email: string;
  phoneNumber: string;
  businessType: string;
  gstNumber?: string;
  panNumber?: string;
  businessAddress: string;
  verificationStatus: 'PENDING' | 'VERIFIED' | 'REJECTED';
  rating: number;
  reviewCount: number;
  totalProducts: number;
  totalSales: number;
  isActive: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface SellerAnalytics {
  sellerId: number;
  sellerName: string;
  businessName: string;
  lastUpdated: string;
  salesMetrics: {
    totalOrders: number;
    ordersToday: number;
    ordersThisWeek: number;
    ordersThisMonth: number;
    ordersThisYear: number;
    averageOrderValue: number;
    orderGrowthRate: number;
    cancelledOrders: number;
    returnedOrders: number;
    cancellationRate: number;
    returnRate: number;
  };
  productMetrics: {
    totalProducts: number;
    activeProducts: number;
    inactiveProducts: number;
    outOfStockProducts: number;
    lowStockProducts: number;
    totalCategories: number;
    averageRating: number;
    totalReviews: number;
    totalViews: number;
  };
  revenueMetrics: {
    totalRevenue: number;
    revenueToday: number;
    revenueThisWeek: number;
    revenueThisMonth: number;
    revenueThisYear: number;
    revenueLastMonth: number;
    revenueGrowthRate: number;
    averageDailyRevenue: number;
    projectedMonthlyRevenue: number;
    commissionPaid: number;
    pendingPayments: number;
  };
}

// Authentication types
export interface LoginRequest {
  username: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
  confirmPassword: string;
  role: 'USER' | 'SELLER';
  firstName?: string;
  lastName?: string;
  phoneNumber?: string;
  businessName?: string;
  businessType?: string;
}

export interface AuthResponse {
  user: User;
  accessToken: string;
  refreshToken: string;
  expiresIn: number;
}

// API Response types
export interface ApiResponse<T> {
  success: boolean;
  data: T;
  message: string;
  timestamp: string;
}

export interface PaginatedResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
}

// Admin types
export interface AdminDashboard {
  systemOverview: {
    systemStatus: string;
    systemLoad: number;
    activeServices: number;
    totalServices: number;
    totalUsers: number;
    activeSellers: number;
    totalProducts: number;
    totalOrders: number;
    totalRevenue: number;
    uptime: string;
  };
  businessMetrics: {
    totalGMV: number;
    totalCommission: number;
    conversionRate: number;
    averageOrderValue: number;
    customerSatisfactionScore: number;
    totalTransactions: number;
    refundAmount: number;
    refundRate: number;
    disputeCount: number;
    marketplaceGrowthRate: number;
  };
  userMetrics: {
    totalUsers: number;
    activeUsers: number;
    newUsersToday: number;
    newUsersThisWeek: number;
    newUsersThisMonth: number;
    userGrowthRate: number;
    userRetentionRate: number;
    averageUserLifetimeValue: number;
    verifiedUsers: number;
    userVerificationRate: number;
    usersByMembershipTier: Record<string, number>;
  };
  lastUpdated: string;
}

// Form types
export interface ProductSearchFilters {
  query?: string;
  category?: string;
  subcategory?: string;
  minPrice?: number;
  maxPrice?: number;
  rating?: number;
  brand?: string;
  inStock?: boolean;
  sortBy?: 'price' | 'rating' | 'newest' | 'popularity';
  sortOrder?: 'asc' | 'desc';
}

export interface OrderFilters {
  status?: string;
  paymentStatus?: string;
  dateFrom?: string;
  dateTo?: string;
  minAmount?: number;
  maxAmount?: number;
}

// UI types
export interface NavItem {
  label: string;
  path: string;
  icon?: React.ComponentType;
  roles?: string[];
  children?: NavItem[];
}

export interface BreadcrumbItem {
  label: string;
  path?: string;
}

export interface TableColumn {
  field: string;
  headerName: string;
  width?: number;
  sortable?: boolean;
  filterable?: boolean;
  renderCell?: (params: any) => React.ReactNode;
}

export interface ChartData {
  name: string;
  value: number;
  [key: string]: any;
}

// Error types
export interface ApiError {
  message: string;
  code: string;
  status: number;
  timestamp: string;
  path: string;
} 