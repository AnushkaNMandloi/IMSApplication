package com.example.user_service.dto;

import com.example.user_service.model.UserAddress;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAddressRequest {

    private UserAddress.AddressType addressType;

    private String label;

    @NotBlank(message = "Recipient name is required")
    private String recipientName;

    @Pattern(regexp = "^[+]?[0-9\\-\\s()]{7,20}$", message = "Invalid phone number format")
    private String phoneNumber;

    @NotBlank(message = "Address line 1 is required")
    private String addressLine1;

    private String addressLine2;

    private String landmark;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "State is required")
    private String state;

    @NotBlank(message = "Postal code is required")
    @Pattern(regexp = "^[0-9A-Za-z\\-\\s]{3,20}$", message = "Invalid postal code format")
    private String postalCode;

    @NotBlank(message = "Country is required")
    private String country = "India";

    private Double latitude;
    private Double longitude;
    private Boolean isDefault = false;
    private String deliveryInstructions;
} 