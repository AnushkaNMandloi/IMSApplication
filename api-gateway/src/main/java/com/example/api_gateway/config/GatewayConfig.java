package com.example.api_gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // Authentication routes (public)
                .route("auth-service", r -> r.path("/api/auth/**")
                        .uri("lb://user-service"))
                
                // User routes (authenticated)
                .route("user-service", r -> r.path("/api/users/**")
                        .uri("lb://user-service"))
                
                // Seller routes (authenticated)
                .route("seller-service", r -> r.path("/api/sellers/**")
                        .uri("lb://seller-service"))
                
                // Item routes (authenticated)
                .route("item-service", r -> r.path("/api/items/**")
                        .uri("lb://item-service"))
                
                // Purchase routes (authenticated)
                .route("purchase-service", r -> r.path("/api/purchases/**")
                        .uri("lb://purchase-service"))
                
                // Admin routes (admin only)
                .route("admin-service", r -> r.path("/api/admin/**")
                        .uri("lb://admin-service"))
                
                // Health check routes
                .route("health-check", r -> r.path("/actuator/**")
                        .uri("http://localhost:8085"))
                
                .build();
    }

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowedOriginPatterns(Arrays.asList("*"));
        corsConfig.setMaxAge(3600L);
        corsConfig.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        corsConfig.setAllowedHeaders(Arrays.asList("*"));
        corsConfig.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        return new CorsWebFilter(source);
    }
} 