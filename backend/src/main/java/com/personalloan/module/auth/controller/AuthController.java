package com.personalloan.module.auth.controller;

import com.personalloan.common.dto.ApiResponse;
import com.personalloan.common.security.CustomUserDetails;
import com.personalloan.module.auth.dto.*;
import com.personalloan.module.auth.entity.User;
import com.personalloan.module.auth.service.AuthService;
import com.personalloan.module.auth.service.EmailVerificationService;
import com.personalloan.module.auth.service.PasswordResetService;
import com.personalloan.module.auth.service.RefreshTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "Endpoints for user registration, session handling, and credential management")
public class AuthController {

    private final AuthService authService;
    private final PasswordResetService passwordResetService;
    private final EmailVerificationService emailVerificationService;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/register")
    @Operation(summary = "Register a new customer account", description = "Onboards a customer user and triggers an activation email verification link")
    public ResponseEntity<ApiResponse<Void>> register(@Valid @RequestBody RegisterRequest request) {
        log.info("REST request to register new user: {}", request.getEmail());
        authService.register(request);
        return ResponseEntity.ok(ApiResponse.success("Registration successful. Please check your email to activate your account."));
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate user credentials", description = "Verifies login credentials and returns JWT access token and refresh token")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        log.info("REST request to login user: {}", request.getEmail());
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh user session", description = "Rotates active refresh token hashes and returns new access and refresh tokens")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        log.info("REST request to rotate refresh token session");
        AuthResponse response = authService.refreshToken(request.getRefreshToken());
        return ResponseEntity.ok(ApiResponse.success("Session refreshed successfully", response));
    }

    @PostMapping("/logout")
    @Operation(summary = "Log out user session", description = "Invalidates the active refresh token in the database")
    public ResponseEntity<ApiResponse<Void>> logout(@Valid @RequestBody RefreshTokenRequest request) {
        log.info("REST request to log out and revoke session");
        User user = refreshTokenService.resolveUserFromToken(request.getRefreshToken());
        refreshTokenService.invalidateToken(user);
        return ResponseEntity.ok(ApiResponse.success("Logged out successfully"));
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Initiate password reset request", description = "Generates a reset link and triggers a secure email to the registered address")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        log.info("REST request to initiate password reset for: {}", request.getEmail());
        passwordResetService.initiatePasswordReset(request.getEmail());
        // Return 200 OK regardless of account existence to avoid user enumeration vulnerability
        return ResponseEntity.ok(ApiResponse.success("If the email is registered, a password reset link has been sent."));
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Reset account password", description = "Updates password using a valid URL verification token hash")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        log.info("REST request to reset account password");
        passwordResetService.resetPassword(
                request.getToken(),
                request.getNewPassword(),
                request.getConfirmPassword()
        );
        return ResponseEntity.ok(ApiResponse.success("Password reset successfully."));
    }

    @PostMapping("/change-password")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Change authenticated password", description = "Updates user password after verifying the existing credentials")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody ChangePasswordRequest request
    ) {
        log.info("REST request to change password for user: {}", userDetails.getUsername());
        authService.changePassword(userDetails.getUser(), request);
        return ResponseEntity.ok(ApiResponse.success("Password changed successfully."));
    }

    @GetMapping("/verify-email")
    @Operation(summary = "Verify account email address", description = "Activates a newly registered customer account using a verification token")
    public ResponseEntity<ApiResponse<Void>> verifyEmail(@RequestParam("token") String token) {
        log.info("REST request to verify email registration");
        emailVerificationService.verifyEmail(token);
        return ResponseEntity.ok(ApiResponse.success("Email verified and account activated successfully."));
    }
}
