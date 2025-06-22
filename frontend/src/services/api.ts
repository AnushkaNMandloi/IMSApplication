import axios, { AxiosInstance, AxiosResponse, AxiosError } from 'axios';
import { toast } from 'react-toastify';
import {
  ApiResponse,
  PaginatedResponse,
  LoginRequest,
  RegisterRequest,
  AuthResponse,
  User,
  UserProfile,
  UserAddress,
  Product,
  ProductSearchFilters,
  Cart,
  CartItem,
  Order,
  OrderFilters,
  Seller,
  SellerAnalytics,
  AdminDashboard,
  ApiError,
} from '../types';

// API Configuration
const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8085/api';

class ApiService {
  private api: AxiosInstance;

  constructor() {
    this.api = axios.create({
      baseURL: API_BASE_URL,
      timeout: 30000,
      headers: {
        'Content-Type': 'application/json',
      },
    });

    this.setupInterceptors();
  }

  private setupInterceptors() {
    // Request interceptor to add auth token
    this.api.interceptors.request.use(
      (config) => {
        const token = localStorage.getItem('accessToken');
        if (token) {
          config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
      },
      (error) => {
        return Promise.reject(error);
      }
    );

    // Response interceptor for error handling
    this.api.interceptors.response.use(
      (response: AxiosResponse) => {
        return response;
      },
      async (error: AxiosError) => {
        const originalRequest = error.config as any;

        if (error.response?.status === 401 && !originalRequest._retry) {
          originalRequest._retry = true;

          try {
            const refreshToken = localStorage.getItem('refreshToken');
            if (refreshToken) {
              const response = await this.api.post('/auth/refresh-token', {
                refreshToken,
              });
              const { accessToken } = response.data;
              localStorage.setItem('accessToken', accessToken);
              return this.api(originalRequest);
            }
                      } catch (refreshError) {
              this.clearAuthData();
              window.location.href = '/login';
            }
        }

        this.handleApiError(error);
        return Promise.reject(error);
      }
    );
  }

  private handleApiError(error: AxiosError) {
    let message = 'An unexpected error occurred';

    if (error.response?.data) {
      const errorData = error.response.data as ApiError;
      message = errorData.message || message;
    } else if (error.message) {
      message = error.message;
    }

    toast.error(message);
  }

  private clearAuthData() {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('user');
  }

  // Authentication APIs
  async login(credentials: LoginRequest): Promise<AuthResponse> {
    const response = await this.api.post<AuthResponse>('/auth/login', credentials);
    return response.data;
  }

  async register(userData: RegisterRequest): Promise<AuthResponse> {
    const response = await this.api.post<AuthResponse>('/auth/register', userData);
    return response.data;
  }

  async logout(): Promise<void> {
    await this.api.post('/auth/logout');
    this.clearAuthData();
  }

  async refreshToken(refreshToken: string): Promise<AuthResponse> {
    const response = await this.api.post<AuthResponse>('/auth/refresh-token', {
      refreshToken,
    });
    return response.data;
  }

  async forgotPassword(email: string): Promise<void> {
    await this.api.post('/auth/forgot-password', { email });
  }

  async resetPassword(token: string, newPassword: string): Promise<void> {
    await this.api.post('/auth/reset-password', { token, newPassword });
  }

  // User APIs
  async getCurrentUser(): Promise<User> {
    const response = await this.api.get<User>('/users/me');
    return response.data;
  }

  async getUserProfile(userId?: number): Promise<UserProfile> {
    const url = userId ? `/users/${userId}/profile` : '/users/me/profile';
    const response = await this.api.get<UserProfile>(url);
    return response.data;
  }

  async updateUserProfile(profileData: Partial<UserProfile>): Promise<UserProfile> {
    const response = await this.api.put<UserProfile>('/users/me/profile', profileData);
    return response.data;
  }

  async getUserAddresses(): Promise<UserAddress[]> {
    const response = await this.api.get<UserAddress[]>('/users/me/addresses');
    return response.data;
  }

  async addUserAddress(addressData: Omit<UserAddress, 'id' | 'createdAt' | 'updatedAt'>): Promise<UserAddress> {
    const response = await this.api.post<UserAddress>('/users/me/addresses', addressData);
    return response.data;
  }

  async updateUserAddress(addressId: number, addressData: Partial<UserAddress>): Promise<UserAddress> {
    const response = await this.api.put<UserAddress>(`/users/me/addresses/${addressId}`, addressData);
    return response.data;
  }

  async deleteUserAddress(addressId: number): Promise<void> {
    await this.api.delete(`/users/me/addresses/${addressId}`);
  }

  async setDefaultAddress(addressId: number): Promise<void> {
    await this.api.put(`/users/me/addresses/${addressId}/default`);
  }

  // Product APIs
  async getProducts(filters?: ProductSearchFilters, page = 0, size = 20): Promise<PaginatedResponse<Product>> {
    const params = new URLSearchParams();
    if (filters) {
      Object.entries(filters).forEach(([key, value]) => {
        if (value !== undefined && value !== null && value !== '') {
          params.append(key, value.toString());
        }
      });
    }
    params.append('page', page.toString());
    params.append('size', size.toString());

    const response = await this.api.get<PaginatedResponse<Product>>(`/items?${params.toString()}`);
    return response.data;
  }

  async getProduct(productId: number): Promise<Product> {
    const response = await this.api.get<Product>(`/items/${productId}`);
    return response.data;
  }

  async getFeaturedProducts(): Promise<Product[]> {
    const response = await this.api.get<Product[]>('/items/featured');
    return response.data;
  }

  async getProductsByCategory(category: string): Promise<Product[]> {
    const response = await this.api.get<Product[]>(`/items/category/${category}`);
    return response.data;
  }

  async searchProducts(query: string): Promise<Product[]> {
    const response = await this.api.get<Product[]>(`/items/search?q=${encodeURIComponent(query)}`);
    return response.data;
  }

  async getProductReviews(productId: number): Promise<any[]> {
    const response = await this.api.get<any[]>(`/items/${productId}/reviews`);
    return response.data;
  }

  async addProductReview(productId: number, reviewData: any): Promise<any> {
    const response = await this.api.post<any>(`/items/${productId}/reviews`, reviewData);
    return response.data;
  }

  // Cart APIs
  async getCart(): Promise<Cart> {
    const response = await this.api.get<Cart>('/cart');
    return response.data;
  }

  async addToCart(productId: number, quantity: number): Promise<CartItem> {
    const response = await this.api.post<CartItem>('/cart/items', {
      productId,
      quantity,
    });
    return response.data;
  }

  async updateCartItem(itemId: number, quantity: number): Promise<CartItem> {
    const response = await this.api.put<CartItem>(`/cart/items/${itemId}`, {
      quantity,
    });
    return response.data;
  }

  async removeFromCart(itemId: number): Promise<void> {
    await this.api.delete(`/cart/items/${itemId}`);
  }

  async clearCart(): Promise<void> {
    await this.api.delete('/cart');
  }

  async validateCart(): Promise<Cart> {
    const response = await this.api.post<Cart>('/cart/validate');
    return response.data;
  }

  // Order APIs
  async getOrders(filters?: OrderFilters, page = 0, size = 20): Promise<PaginatedResponse<Order>> {
    const params = new URLSearchParams();
    if (filters) {
      Object.entries(filters).forEach(([key, value]) => {
        if (value !== undefined && value !== null && value !== '') {
          params.append(key, value.toString());
        }
      });
    }
    params.append('page', page.toString());
    params.append('size', size.toString());

    const response = await this.api.get<PaginatedResponse<Order>>(`/orders?${params.toString()}`);
    return response.data;
  }

  async getOrder(orderId: number): Promise<Order> {
    const response = await this.api.get<Order>(`/orders/${orderId}`);
    return response.data;
  }

  async createOrder(orderData: any): Promise<Order> {
    const response = await this.api.post<Order>('/orders', orderData);
    return response.data;
  }

  async cancelOrder(orderId: number, reason: string): Promise<Order> {
    const response = await this.api.put<Order>(`/orders/${orderId}/cancel`, {
      reason,
    });
    return response.data;
  }

  async returnOrder(orderId: number, reason: string): Promise<Order> {
    const response = await this.api.put<Order>(`/orders/${orderId}/return`, {
      reason,
    });
    return response.data;
  }

  async trackOrder(trackingNumber: string): Promise<any> {
    const response = await this.api.get<any>(`/orders/track/${trackingNumber}`);
    return response.data;
  }

  // Seller APIs
  async getSellerProfile(): Promise<Seller> {
    const response = await this.api.get<Seller>('/sellers/me');
    return response.data;
  }

  async updateSellerProfile(sellerData: Partial<Seller>): Promise<Seller> {
    const response = await this.api.put<Seller>('/sellers/me', sellerData);
    return response.data;
  }

  async getSellerAnalytics(): Promise<SellerAnalytics> {
    const response = await this.api.get<SellerAnalytics>('/sellers/me/analytics');
    return response.data;
  }

  async getSellerProducts(page = 0, size = 20): Promise<PaginatedResponse<Product>> {
    const response = await this.api.get<PaginatedResponse<Product>>(`/sellers/me/products?page=${page}&size=${size}`);
    return response.data;
  }

  async addSellerProduct(productData: any): Promise<Product> {
    const response = await this.api.post<Product>('/sellers/me/products', productData);
    return response.data;
  }

  async updateSellerProduct(productId: number, productData: any): Promise<Product> {
    const response = await this.api.put<Product>(`/sellers/me/products/${productId}`, productData);
    return response.data;
  }

  async deleteSellerProduct(productId: number): Promise<void> {
    await this.api.delete(`/sellers/me/products/${productId}`);
  }

  async getSellerOrders(page = 0, size = 20): Promise<PaginatedResponse<Order>> {
    const response = await this.api.get<PaginatedResponse<Order>>(`/sellers/me/orders?page=${page}&size=${size}`);
    return response.data;
  }

  async updateOrderStatus(orderId: number, status: string, notes?: string): Promise<Order> {
    const response = await this.api.put<Order>(`/orders/${orderId}/status`, {
      status,
      notes,
    });
    return response.data;
  }

  // Admin APIs
  async getAdminDashboard(): Promise<AdminDashboard> {
    const response = await this.api.get<AdminDashboard>('/admin/dashboard');
    return response.data;
  }

  async getAdminUsers(page = 0, size = 20, filters?: any): Promise<PaginatedResponse<User>> {
    const params = new URLSearchParams();
    if (filters) {
      Object.entries(filters).forEach(([key, value]) => {
        if (value !== undefined && value !== null && value !== '') {
          params.append(key, value.toString());
        }
      });
    }
    params.append('page', page.toString());
    params.append('size', size.toString());

    const response = await this.api.get<PaginatedResponse<User>>(`/admin/users?${params.toString()}`);
    return response.data;
  }

  async getAdminSellers(page = 0, size = 20, filters?: any): Promise<PaginatedResponse<Seller>> {
    const params = new URLSearchParams();
    if (filters) {
      Object.entries(filters).forEach(([key, value]) => {
        if (value !== undefined && value !== null && value !== '') {
          params.append(key, value.toString());
        }
      });
    }
    params.append('page', page.toString());
    params.append('size', size.toString());

    const response = await this.api.get<PaginatedResponse<Seller>>(`/admin/sellers?${params.toString()}`);
    return response.data;
  }

  async getAdminProducts(page = 0, size = 20, filters?: any): Promise<PaginatedResponse<Product>> {
    const params = new URLSearchParams();
    if (filters) {
      Object.entries(filters).forEach(([key, value]) => {
        if (value !== undefined && value !== null && value !== '') {
          params.append(key, value.toString());
        }
      });
    }
    params.append('page', page.toString());
    params.append('size', size.toString());

    const response = await this.api.get<PaginatedResponse<Product>>(`/admin/products?${params.toString()}`);
    return response.data;
  }

  async getAdminOrders(page = 0, size = 20, filters?: any): Promise<PaginatedResponse<Order>> {
    const params = new URLSearchParams();
    if (filters) {
      Object.entries(filters).forEach(([key, value]) => {
        if (value !== undefined && value !== null && value !== '') {
          params.append(key, value.toString());
        }
      });
    }
    params.append('page', page.toString());
    params.append('size', size.toString());

    const response = await this.api.get<PaginatedResponse<Order>>(`/admin/orders?${params.toString()}`);
    return response.data;
  }

  async updateUserStatus(userId: number, status: string, reason?: string): Promise<void> {
    await this.api.put(`/admin/users/${userId}/status`, { status, reason });
  }

  async verifyUser(userId: number): Promise<void> {
    await this.api.put(`/admin/users/${userId}/verify`);
  }

  async verifySeller(sellerId: number, status: string, notes?: string): Promise<void> {
    await this.api.put(`/admin/sellers/${sellerId}/verify`, { status, notes });
  }

  async approveProduct(productId: number, notes?: string): Promise<void> {
    await this.api.put(`/admin/products/${productId}/approve`, { notes });
  }

  async rejectProduct(productId: number, reason: string): Promise<void> {
    await this.api.put(`/admin/products/${productId}/reject`, { reason });
  }

  // File upload
  async uploadFile(file: File, type: 'profile' | 'product' | 'document'): Promise<string> {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('type', type);

    const response = await this.api.post<{ url: string }>('/upload', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });

    return response.data.url;
  }
}

export const apiService = new ApiService();
export default apiService; 