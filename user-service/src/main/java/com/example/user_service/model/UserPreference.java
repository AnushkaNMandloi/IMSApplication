package com.example.user_service.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_preferences")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class UserPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_profile_id", nullable = false)
    private UserProfile userProfile;

    @Column(name = "preference_category", nullable = false)
    @Enumerated(EnumType.STRING)
    private PreferenceCategory category;

    @Column(name = "preference_key", nullable = false, length = 100)
    private String preferenceKey;

    @Column(name = "preference_value", length = 500)
    private String preferenceValue;

    @Column(name = "data_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private DataType dataType = DataType.STRING;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "description", length = 255)
    private String description;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum PreferenceCategory {
        NOTIFICATION,
        PRIVACY,
        SHOPPING,
        DISPLAY,
        COMMUNICATION,
        SECURITY,
        MARKETING,
        ACCOUNT
    }

    public enum DataType {
        STRING,
        BOOLEAN,
        INTEGER,
        DECIMAL,
        JSON,
        LIST
    }

    // Helper methods
    public Boolean getBooleanValue() {
        if (dataType == DataType.BOOLEAN && preferenceValue != null) {
            return Boolean.parseBoolean(preferenceValue);
        }
        return null;
    }

    public Integer getIntegerValue() {
        if (dataType == DataType.INTEGER && preferenceValue != null) {
            try {
                return Integer.parseInt(preferenceValue);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    public Double getDecimalValue() {
        if (dataType == DataType.DECIMAL && preferenceValue != null) {
            try {
                return Double.parseDouble(preferenceValue);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    public void setBooleanValue(Boolean value) {
        this.dataType = DataType.BOOLEAN;
        this.preferenceValue = value != null ? value.toString() : null;
    }

    public void setIntegerValue(Integer value) {
        this.dataType = DataType.INTEGER;
        this.preferenceValue = value != null ? value.toString() : null;
    }

    public void setDecimalValue(Double value) {
        this.dataType = DataType.DECIMAL;
        this.preferenceValue = value != null ? value.toString() : null;
    }
} 