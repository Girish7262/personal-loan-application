package com.personalloan.module.auth.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "email", unique = true, nullable = false, length = 255)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "mobile_number", unique = true, nullable = false, length = 15)
    private String mobileNumber;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Column(name = "failed_login_attempts", nullable = false)
    private Integer failedLoginAttempts;

    @Column(name = "lockout_until")
    private LocalDateTime lockoutUntil;

    @Column(name = "email_verification_token", length = 255)
    private String emailVerificationToken;

    /**
     * Stores the cryptographically secure SHA-256 hash of the password reset token.
     * Raw tokens are sent to user emails and never stored directly in the database.
     * Storing the token directly on the User record ensures a strict 1:1 relationship;
     * generating a new token automatically overwrites and invalidates any previous token.
     */
    @Column(name = "password_reset_token", length = 255)
    private String passwordResetToken;

    /**
     * Timestamp indicating when the password reset token expires (TTL).
     */
    @Column(name = "reset_token_expiry")
    private LocalDateTime resetTokenExpiry;

    @Column(name = "force_password_change", nullable = false)
    private Boolean forcePasswordChange;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @Column(name = "updated_by", length = 100)
    private String updatedBy;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;

    @Column(name = "last_successful_login_at")
    private LocalDateTime lastSuccessfulLoginAt;

    @Column(name = "refresh_token_hash", length = 255)
    private String refreshTokenHash;

    @Column(name = "refresh_token_expiry")
    private LocalDateTime refreshTokenExpiry;

    @Column(name = "email_verification_expiry")
    private LocalDateTime emailVerificationExpiry;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (failedLoginAttempts == null) failedLoginAttempts = 0;
        if (forcePasswordChange == null) forcePasswordChange = false;
        if (isDeleted == null) isDeleted = false;
        if (status == null) status = "INACTIVE";
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
