package com.personalloan.module.customer.api;

import com.personalloan.module.customer.api.dto.*;

import java.util.Optional;

public interface CustomerFacade {

    /**
     * Retrieves the profile summary details for a specific user ID to support downstream loan calculations.
     *
     * @param userId the user ID to check
     * @return an Optional containing the CustomerSummary if found, empty otherwise
     */
    Optional<CustomerSummary> getCustomerSummary(Long userId);

    /**
     * Retrieves the profile summary details for a specific customer ID.
     *
     * @param customerId the customer ID to check
     * @return an Optional containing the CustomerSummary if found, empty otherwise
     */
    Optional<CustomerSummary> getCustomerSummaryByCustomerId(Long customerId);

    /**
     * Retrieves the profile associated with a user ID, falling back to a shell response with INCOMPLETE status if none exists.
     *
     * @param userId the user ID
     * @return the customer profile response
     */
    CustomerProfileResponse getProfile(Long userId);

    /**
     * Creates a profile for the user. Fails with a duplicate conflict if one already exists.
     *
     * @param userId the user ID
     * @param request the profile creation payload
     * @param currentUserEmail the active user's email
     * @return the created profile details
     */
    CustomerProfileResponse createProfile(Long userId, CustomerProfileRequest request, String currentUserEmail);

    /**
     * Replaces the entire profile details of the user.
     *
     * @param userId the user ID
     * @param request the profile details update payload
     * @param currentUserEmail the active user's email
     * @return the updated profile details
     */
    CustomerProfileResponse updateProfile(Long userId, CustomerProfileRequest request, String currentUserEmail);

    /**
     * Performs a partial update (patching) on selected fields, leaving omitted properties untouched.
     *
     * @param userId the user ID
     * @param request the partial profile fields
     * @param currentUserEmail the active user's email
     * @return the patched profile details
     */
    CustomerProfileResponse patchProfile(Long userId, CustomerProfileRequest request, String currentUserEmail);

    /**
     * Analyzes customer profile progress to return completion metrics.
     *
     * @param userId the user ID
     * @return the profile completion metrics DTO
     */
    ProfileCompletionResponse getProfileCompletion(Long userId);
}
