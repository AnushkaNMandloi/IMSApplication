package com.example.item_service.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "product_reviews")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class ProductReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "username", length = 100)
    private String username;

    @Column(name = "order_id")
    private Long orderId; // Link to the order where this product was purchased

    @Column(name = "rating", nullable = false)
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    private Integer rating;

    @Column(name = "title", length = 200)
    @Size(max = 200, message = "Review title must not exceed 200 characters")
    private String title;

    @Column(name = "review_text", length = 2000)
    @Size(max = 2000, message = "Review text must not exceed 2000 characters")
    private String reviewText;

    @ElementCollection
    @CollectionTable(name = "review_images", joinColumns = @JoinColumn(name = "review_id"))
    @Column(name = "image_url")
    private List<String> imageUrls;

    @Column(name = "verified_purchase", nullable = false)
    private Boolean verifiedPurchase = false;

    @Column(name = "helpful_count", nullable = false)
    private Integer helpfulCount = 0;

    @Column(name = "not_helpful_count", nullable = false)
    private Integer notHelpfulCount = 0;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ReviewStatus status = ReviewStatus.PENDING;

    @Column(name = "moderation_notes", length = 500)
    private String moderationNotes;

    @Column(name = "moderated_by")
    private Long moderatedBy;

    @Column(name = "moderated_at")
    private LocalDateTime moderatedAt;

    // Seller response
    @Column(name = "seller_response", length = 1000)
    private String sellerResponse;

    @Column(name = "seller_response_date")
    private LocalDateTime sellerResponseDate;

    // Review metadata
    @Column(name = "review_source")
    @Enumerated(EnumType.STRING)
    private ReviewSource reviewSource = ReviewSource.WEBSITE;

    @Column(name = "device_type")
    private String deviceType;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    // Flags and reports
    @Column(name = "is_flagged", nullable = false)
    private Boolean isFlagged = false;

    @Column(name = "flag_count", nullable = false)
    private Integer flagCount = 0;

    @Column(name = "flag_reasons", length = 500)
    private String flagReasons;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum ReviewStatus {
        PENDING,     // Waiting for moderation
        APPROVED,    // Approved and visible
        REJECTED,    // Rejected by moderation
        HIDDEN,      // Hidden due to reports
        DELETED      // Soft deleted
    }

    public enum ReviewSource {
        WEBSITE,
        MOBILE_APP,
        EMAIL_CAMPAIGN,
        SOCIAL_MEDIA,
        THIRD_PARTY
    }

    // Helper methods
    public Double getHelpfulnessRatio() {
        int totalVotes = helpfulCount + notHelpfulCount;
        if (totalVotes == 0) return 0.0;
        return (double) helpfulCount / totalVotes;
    }

    public boolean isHighQualityReview() {
        return reviewText != null && reviewText.length() >= 50 
               && rating != null 
               && verifiedPurchase 
               && status == ReviewStatus.APPROVED;
    }

    public void addHelpfulVote() {
        this.helpfulCount = (this.helpfulCount != null ? this.helpfulCount : 0) + 1;
    }

    public void addNotHelpfulVote() {
        this.notHelpfulCount = (this.notHelpfulCount != null ? this.notHelpfulCount : 0) + 1;
    }

    public void flagReview(String reason) {
        this.isFlagged = true;
        this.flagCount = (this.flagCount != null ? this.flagCount : 0) + 1;
        if (this.flagReasons == null) {
            this.flagReasons = reason;
        } else {
            this.flagReasons += "; " + reason;
        }
    }

    public void approve(Long moderatorId, String notes) {
        this.status = ReviewStatus.APPROVED;
        this.moderatedBy = moderatorId;
        this.moderatedAt = LocalDateTime.now();
        this.moderationNotes = notes;
    }

    public void reject(Long moderatorId, String notes) {
        this.status = ReviewStatus.REJECTED;
        this.moderatedBy = moderatorId;
        this.moderatedAt = LocalDateTime.now();
        this.moderationNotes = notes;
    }

    public void addSellerResponse(String response) {
        this.sellerResponse = response;
        this.sellerResponseDate = LocalDateTime.now();
    }
} 