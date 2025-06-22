package com.example.seller_service.dto;

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
public class SellerAnalyticsResponse {

    // Basic metrics
    private Long sellerId;
    private String sellerName;
    private String businessName;
    private LocalDateTime lastUpdated;

    // Sales metrics
    private SalesMetrics salesMetrics;
    private ProductMetrics productMetrics;
    private CustomerMetrics customerMetrics;
    private RevenueMetrics revenueMetrics;

    // Charts data
    private List<DailySalesData> dailySales;
    private List<ProductSalesData> topSellingProducts;
    private List<CategorySalesData> salesByCategory;
    private List<MonthlyRevenueData> monthlyRevenue;

    // Performance indicators
    private PerformanceIndicators performance;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SalesMetrics {
        private Long totalOrders;
        private Long ordersToday;
        private Long ordersThisWeek;
        private Long ordersThisMonth;
        private Long ordersThisYear;
        private BigDecimal averageOrderValue;
        private Double orderGrowthRate; // percentage
        private Long cancelledOrders;
        private Long returnedOrders;
        private Double cancellationRate; // percentage
        private Double returnRate; // percentage
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProductMetrics {
        private Long totalProducts;
        private Long activeProducts;
        private Long inactiveProducts;
        private Long outOfStockProducts;
        private Long lowStockProducts; // less than 10 units
        private Long totalCategories;
        private Double averageRating;
        private Long totalReviews;
        private Long totalViews;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CustomerMetrics {
        private Long totalCustomers;
        private Long newCustomersThisMonth;
        private Long repeatCustomers;
        private Double customerRetentionRate; // percentage
        private BigDecimal averageCustomerValue;
        private Long customerReviews;
        private Double averageCustomerRating;
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
        private BigDecimal revenueLastMonth;
        private Double revenueGrowthRate; // percentage month-over-month
        private BigDecimal averageDailyRevenue;
        private BigDecimal projectedMonthlyRevenue;
        private BigDecimal commissionPaid;
        private BigDecimal pendingPayments;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DailySalesData {
        private LocalDate date;
        private Long orders;
        private BigDecimal revenue;
        private Long units;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProductSalesData {
        private Long productId;
        private String productName;
        private String category;
        private Long unitsSold;
        private BigDecimal revenue;
        private Double rating;
        private String imageUrl;
        private BigDecimal price;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CategorySalesData {
        private String category;
        private Long productCount;
        private Long unitsSold;
        private BigDecimal revenue;
        private Double percentage;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MonthlyRevenueData {
        private String month; // "2024-01"
        private BigDecimal revenue;
        private Long orders;
        private BigDecimal growth; // percentage from previous month
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PerformanceIndicators {
        private String performanceRating; // "EXCELLENT", "GOOD", "AVERAGE", "POOR"
        private Double sellerScore; // 0-100
        private List<String> strengths;
        private List<String> improvementAreas;
        private Map<String, Double> kpiScores; // KPI name -> score
        private String nextMilestone;
        private Double progressToNextMilestone; // percentage
    }
} 