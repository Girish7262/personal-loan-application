package com.personalloan.module.customer.api;

import com.personalloan.common.dto.ApiResponse;
import com.personalloan.common.security.CustomUserDetails;
import com.personalloan.module.customer.api.dto.CustomerProfileRequest;
import com.personalloan.module.customer.api.dto.CustomerProfileResponse;
import com.personalloan.module.customer.api.dto.ProfileCompletionResponse;
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
@RequestMapping("/api/v1/customer/profile")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Customer Profile", description = "Endpoints for managing customer profile onboarding, security validation, and completion statistics")
public class CustomerController {

    private final CustomerFacade customerFacade;

    @GetMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Get customer profile details", description = "Retrieves the active customer profile. If none exists, returns an INCOMPLETE status response envelope.")
    public ResponseEntity<ApiResponse<CustomerProfileResponse>> getProfile(@AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("REST request to fetch profile for user ID: {}", userDetails.getUserId());
        CustomerProfileResponse response = customerFacade.getProfile(userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success("Profile details retrieved", response));
    }

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Create customer profile", description = "Registers a new profile. Fails with HTTP 409 Conflict if a profile already exists for the authenticated user context.")
    public ResponseEntity<ApiResponse<CustomerProfileResponse>> createProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody CustomerProfileRequest request) {
        log.info("REST request to create profile for user ID: {}", userDetails.getUserId());
        CustomerProfileResponse response = customerFacade.createProfile(userDetails.getUserId(), request, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Profile created successfully", response));
    }

    @PutMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Update customer profile", description = "Completely replaces the existing profile details.")
    public ResponseEntity<ApiResponse<CustomerProfileResponse>> updateProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody CustomerProfileRequest request) {
        log.info("REST request to replace profile for user ID: {}", userDetails.getUserId());
        CustomerProfileResponse response = customerFacade.updateProfile(userDetails.getUserId(), request, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", response));
    }

    @PatchMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Patch customer profile", description = "Partially updates profile properties, ignoring null parameter inputs.")
    public ResponseEntity<ApiResponse<CustomerProfileResponse>> patchProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody CustomerProfileRequest request) {
        log.info("REST request to patch profile for user ID: {}", userDetails.getUserId());
        CustomerProfileResponse response = customerFacade.patchProfile(userDetails.getUserId(), request, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Profile patched successfully", response));
    }

    @GetMapping("/completion")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Get profile completion statistics", description = "Analyzes customer profile completion rate, status enum, and lists missing fields.")
    public ResponseEntity<ApiResponse<ProfileCompletionResponse>> getProfileCompletion(@AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("REST request to fetch profile completion metrics for user ID: {}", userDetails.getUserId());
        ProfileCompletionResponse response = customerFacade.getProfileCompletion(userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success("Completion statistics calculated", response));
    }
}
