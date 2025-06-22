package com.example.user_service.dto;

import com.example.user_service.model.UserProfile;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileRequest {

    @Size(max = 50, message = "First name must not exceed 50 characters")
    private String firstName;

    @Size(max = 50, message = "Last name must not exceed 50 characters")
    private String lastName;

    private String profilePictureUrl;

    private LocalDate dateOfBirth;

    private UserProfile.Gender gender;

    @Pattern(regexp = "^[+]?[0-9\\-\\s()]{7,20}$", message = "Invalid phone number format")
    private String phoneNumber;

    @Email(message = "Invalid alternate email format")
    private String alternateEmail;

    @Size(max = 500, message = "Bio must not exceed 500 characters")
    private String bio;

    private String websiteUrl;

    @Size(max = 100, message = "Occupation must not exceed 100 characters")
    private String occupation;

    @Size(max = 100, message = "Company must not exceed 100 characters")
    private String company;

    private String facebookUrl;
    private String twitterUrl;
    private String linkedinUrl;
    private String instagramUrl;

    private UserProfile.ProfileVisibility profileVisibility;
    private Boolean emailNotifications;
    private Boolean smsNotifications;
    private Boolean marketingEmails;
} 