package com.example.admin_service.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminDashboardResponse {

    // System overview
    private SystemOverview systemOverview;
    private BusinessMetrics businessMetrics;
    private UserMetrics userMetrics;
    private SellerMetrics sellerMetrics;
    private ProductMetrics productMetrics;
    private OrderMetrics orderMetrics;
    private RevenueMetrics revenueMetrics;

    // Charts and graphs data
    private List<DailyStatsData> dailyStats;
    private List<MonthlyRevenueData> monthlyRevenue;
    private List<CategoryPerformanceData> categoryPerformance;
    private List<TopSellerData> topSellers;
    private List<TopProductData> topProducts;

    // Real-time data
    private RealTimeMetrics realTimeMetrics;
    private List<RecentActivityData> recentActivities;
    private List<SystemAlertData> systemAlerts;

    private LocalDateTime lastUpdated;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SystemOverview {
        private String systemStatus; // "HEALTHY", "WARNING", "CRITICAL"
        private Double systemLoad; // 0-100%
        private Integer activeServices;
        private Integer totalServices;
        private Long totalUsers;
        private Long activeSellers;
        private Long totalProducts;
        private Long totalOrders;
        private BigDecimal totalRevenue;
        private String uptime;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BusinessMetrics {
        private BigDecimal totalGMV; // Gross Merchandise Value
        private BigDecimal totalCommission;
        private Double conversionRate; // percentage
        private BigDecimal averageOrderValue;
        private Double customerSatisfactionScore; // 0-5
        private Long totalTransactions;
        private BigDecimal refundAmount;
        private Double refundRate; // percentage
        private Long disputeCount;
        private Double marketplaceGrowthRate; // percentage
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserMetrics {
        private Long totalUsers;
        private Long activeUsers; // logged in last 30 days
        private Long newUsersToday;
        private Long newUsersThisWeek;
        private Long newUsersThisMonth;
        private Double userGrowthRate; // percentage
        private Double userRetentionRate; // percentage
        private BigDecimal averageUserLifetimeValue;
        private Long verifiedUsers;
        private Double userVerificationRate; // percentage
        private Map<String, Long> usersByMembershipTier;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SellerMetrics {
        private Long totalSellers;
        private Long activeSellers; // with sales in last 30 days
        private Long verifiedSellers;
        private Long newSellersToday;
        private Long newSellersThisMonth;
        private Double sellerGrowthRate; // percentage
        private Double averageSellerRating;
        private Long sellersAwaitingVerification;
        private BigDecimal averageSellerRevenue;
        private Long topPerformingSellers; // top 10%
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProductMetrics {
        private Long totalProducts;
        private Long activeProducts;
        private Long outOfStockProducts;
        private Long lowStockProducts;
        private Long newProductsToday;
        private Long newProductsThisMonth;
        private Double productGrowthRate; // percentage
        private Double averageProductRating;
        private Long totalReviews;
        private Long productsAwaitingApproval;
        private Map<String, Long> productsByCategory;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderMetrics {
        private Long totalOrders;
        private Long ordersToday;
        private Long ordersThisWeek;
        private Long ordersThisMonth;
        private Double orderGrowthRate; // percentage
        private Long pendingOrders;
        private Long processingOrders;
        private Long shippedOrders;
        private Long deliveredOrders;
        private Long cancelledOrders;
        private Long returnedOrders;
        private Double orderFulfillmentRate; // percentage
        private Double averageDeliveryTime; // in days
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RevenueMetrics {
        private BigDecimal totalRevenue;
        private BigDecimal revenueToday;
        private BigDecimal revenueThisWeek;
        private BigDecimal revenueThisMonth;
        private BigDecimal revenueThisYear;
        private Double revenueGrowthRate; // percentage
        private BigDecimal commissionEarned;
        private BigDecimal paymentProcessingFees;
        private BigDecimal netProfit;
        private BigDecimal projectedMonthlyRevenue;
        private Map<String, BigDecimal> revenueByPaymentMethod;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DailyStatsData {
        private LocalDate date;
        private Long newUsers;
        private Long newSellers;
        private Long newProducts;
        private Long orders;
        private BigDecimal revenue;
        private Long activeUsers;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MonthlyRevenueData {
        private String month; // "2024-01"
        private BigDecimal revenue;
        private BigDecimal commission;
        private Long orders;
        private Double growth; // percentage
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CategoryPerformanceData {
        private String category;
        private Long productCount;
        private Long orders;
        private BigDecimal revenue;
        private Double averageRating;
        private Double marketShare; // percentage
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TopSellerData {
        private Long sellerId;
        private String sellerName;
        private String businessName;
        private BigDecimal revenue;
        private Long orders;
        private Double rating;
        private String verificationStatus;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TopProductData {
        private Long productId;
        private String productName;
        private String category;
        private String sellerName;
        private Long unitsSold;
        private BigDecimal revenue;
        private Double rating;
        private String imageUrl;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RealTimeMetrics {
        private Long onlineUsers;
        private Long activeOrders;
        private BigDecimal todayRevenue;
        private Long todayOrders;
        private Long pendingReviews;
        private Long pendingDisputes;
        private Long systemErrors;
        private Double serverResponseTime; // in milliseconds
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RecentActivityData {
        private String activityType; // "ORDER", "USER_REGISTRATION", "SELLER_VERIFICATION", etc.
        private String description;
        private LocalDateTime timestamp;
        private String userId;
        private String status;
        private String priority; // "HIGH", "MEDIUM", "LOW"
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SystemAlertData {
        private String alertType; // "ERROR", "WARNING", "INFO"
        private String title;
        private String message;
        private String severity; // "CRITICAL", "HIGH", "MEDIUM", "LOW"
        private LocalDateTime timestamp;
        private String source; // service name
        private Boolean isResolved;
        private String resolvedBy;
    }
} 