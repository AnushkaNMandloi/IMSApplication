package com.example.user_service.dto;

import com.example.user_service.model.UserAddress;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAddressResponse {

    private Long id;
    private UserAddress.AddressType addressType;
    private String label;
    private String recipientName;
    private String phoneNumber;
    private String addressLine1;
    private String addressLine2;
    private String landmark;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    private Double latitude;
    private Double longitude;
    private Boolean isDefault;
    private Boolean isActive;
    private String deliveryInstructions;
    private String fullAddress;
    private Integer usageCount;
    private LocalDateTime lastUsedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public UserAddressResponse(UserAddress address) {
        this.id = address.getId();
        this.addressType = address.getAddressType();
        this.label = address.getLabel();
        this.recipientName = address.getRecipientName();
        this.phoneNumber = address.getPhoneNumber();
        this.addressLine1 = address.getAddressLine1();
        this.addressLine2 = address.getAddressLine2();
        this.landmark = address.getLandmark();
        this.city = address.getCity();
        this.state = address.getState();
        this.postalCode = address.getPostalCode();
        this.country = address.getCountry();
        this.latitude = address.getLatitude();
        this.longitude = address.getLongitude();
        this.isDefault = address.getIsDefault();
        this.isActive = address.getIsActive();
        this.deliveryInstructions = address.getDeliveryInstructions();
        this.fullAddress = address.getFullAddress();
        this.usageCount = address.getUsageCount();
        this.lastUsedAt = address.getLastUsedAt();
        this.createdAt = address.getCreatedAt();
        this.updatedAt = address.getUpdatedAt();
    }
} 