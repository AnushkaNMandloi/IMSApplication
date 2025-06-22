package com.example.user_service.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_addresses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class UserAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_profile_id", nullable = false)
    private UserProfile userProfile;

    @Column(name = "address_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private AddressType addressType;

    @Column(name = "label", length = 50)
    private String label; // e.g., "Home", "Office", "Mom's House"

    @Column(name = "recipient_name", length = 100)
    @NotBlank(message = "Recipient name is required")
    private String recipientName;

    @Column(name = "phone_number", length = 20)
    @Pattern(regexp = "^[+]?[0-9\\-\\s()]{7,20}$", message = "Invalid phone number format")
    private String phoneNumber;

    @Column(name = "address_line_1", length = 255, nullable = false)
    @NotBlank(message = "Address line 1 is required")
    private String addressLine1;

    @Column(name = "address_line_2", length = 255)
    private String addressLine2;

    @Column(name = "landmark", length = 100)
    private String landmark;

    @Column(name = "city", length = 100, nullable = false)
    @NotBlank(message = "City is required")
    private String city;

    @Column(name = "state", length = 100, nullable = false)
    @NotBlank(message = "State is required")
    private String state;

    @Column(name = "postal_code", length = 20, nullable = false)
    @NotBlank(message = "Postal code is required")
    @Pattern(regexp = "^[0-9A-Za-z\\-\\s]{3,20}$", message = "Invalid postal code format")
    private String postalCode;

    @Column(name = "country", length = 100, nullable = false)
    @NotBlank(message = "Country is required")
    private String country = "India";

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "is_default", nullable = false)
    private Boolean isDefault = false;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "delivery_instructions", length = 500)
    private String deliveryInstructions;

    @Column(name = "usage_count", nullable = false)
    private Integer usageCount = 0;

    @Column(name = "last_used_at")
    private LocalDateTime lastUsedAt;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum AddressType {
        HOME, OFFICE, BILLING, SHIPPING, OTHER
    }

    // Helper methods
    public String getFullAddress() {
        StringBuilder address = new StringBuilder();
        address.append(addressLine1);
        
        if (addressLine2 != null && !addressLine2.trim().isEmpty()) {
            address.append(", ").append(addressLine2);
        }
        
        if (landmark != null && !landmark.trim().isEmpty()) {
            address.append(", ").append(landmark);
        }
        
        address.append(", ").append(city);
        address.append(", ").append(state);
        address.append(" - ").append(postalCode);
        address.append(", ").append(country);
        
        return address.toString();
    }

    public void incrementUsage() {
        this.usageCount = (this.usageCount != null ? this.usageCount : 0) + 1;
        this.lastUsedAt = LocalDateTime.now();
    }

    public boolean isValidForDelivery() {
        return isActive && addressLine1 != null && !addressLine1.trim().isEmpty() 
               && city != null && !city.trim().isEmpty()
               && state != null && !state.trim().isEmpty()
               && postalCode != null && !postalCode.trim().isEmpty();
    }
} 