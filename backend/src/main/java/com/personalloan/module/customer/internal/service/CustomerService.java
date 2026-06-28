package com.personalloan.module.customer.internal.service;

import com.personalloan.common.exception.BusinessException;
import com.personalloan.common.exception.ResourceNotFoundException;
import com.personalloan.module.customer.api.dto.*;
import com.personalloan.module.customer.internal.entity.CustomerProfile;
import com.personalloan.module.customer.internal.mapper.CustomerMapper;
import com.personalloan.module.customer.internal.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final ValidationService validationService;
    private final EncryptionService encryptionService;
    private final CustomerMapper customerMapper;

    /**
     * Retrieves the profile associated with a user ID.
     * If no profile exists, returns a shell response with status INCOMPLETE.
     *
     * @param userId the user ID owning the profile
     * @return the profile response DTO containing masked numbers
     */
    @Transactional(readOnly = true)
    public CustomerProfileResponse getProfile(Long userId) {
        log.info("Retrieving customer profile details for user ID: {}", userId);
        Optional<CustomerProfile> profileOpt = customerRepository.findByUserId(userId);

        if (profileOpt.isEmpty()) {
            log.info("No profile found for user ID: {}. Returning INCOMPLETE shell.", userId);
            return CustomerProfileResponse.builder()
                    .userId(userId)
                    .profileStatus(ProfileStatus.INCOMPLETE.name())
                    .build();
        }

        CustomerProfile profile = profileOpt.get();
        String decryptedAadhaar = encryptionService.decrypt(profile.getAadhaarNumber());
        return mapToResponse(profile, decryptedAadhaar);
    }

    /**
     * Creates a profile for the user. Throws HTTP 409 (BusinessException) if a profile already exists.
     */
    @Transactional
    public CustomerProfileResponse createProfile(Long userId, CustomerProfileRequest request, String currentUserEmail) {
        log.info("Creating profile for user ID: {}", userId);
        if (customerRepository.findByUserId(userId).isPresent()) {
            throw new com.personalloan.common.exception.DuplicateResourceException("Profile already exists for this user. Use PUT or PATCH to update.");
        }

        CustomerProfile profile = CustomerProfile.builder()
                .userId(userId)
                .createdBy(currentUserEmail)
                .build();

        return saveProfile(profile, request, currentUserEmail);
    }

    /**
     * Updates/replaces the entire profile details of the user.
     */
    @Transactional
    public CustomerProfileResponse updateProfile(Long userId, CustomerProfileRequest request, String currentUserEmail) {
        log.info("Replacing complete profile details for user ID: {}", userId);
        CustomerProfile profile = customerRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer profile not found"));

        profile.setUpdatedBy(currentUserEmail);
        return saveProfile(profile, request, currentUserEmail);
    }

    /**
     * Performs a partial update (patching) on selected fields, leaving omitted properties untouched.
     */
    @Transactional
    public CustomerProfileResponse patchProfile(Long userId, CustomerProfileRequest request, String currentUserEmail) {
        log.info("Patching partial profile details for user ID: {}", userId);
        CustomerProfile profile = customerRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer profile not found"));

        // Validate format only for provided/non-null fields in the request
        if (request.getDateOfBirth() != null) {
            validationService.validateAge(request.getDateOfBirth());
        }
        if (request.getPanNumber() != null) {
            String pan = request.getPanNumber().trim();
            validationService.validatePan(pan);
            // Check PAN uniqueness
            Optional<CustomerProfile> existingPan = customerRepository.findByPanNumber(pan);
            if (existingPan.isPresent() && !existingPan.get().getUserId().equals(userId)) {
                throw new BusinessException("PAN number is already registered by another customer");
            }
            profile.setPanNumber(pan);
        }
        if (request.getAadhaarNumber() != null) {
            String aadhaar = request.getAadhaarNumber().trim();
            validationService.validateAadhaar(aadhaar);
            // Check Aadhaar uniqueness
            String encryptedAadhaar = encryptionService.encrypt(aadhaar);
            Optional<CustomerProfile> existingAadhaar = customerRepository.findByAadhaarNumber(encryptedAadhaar);
            if (existingAadhaar.isPresent() && !existingAadhaar.get().getUserId().equals(userId)) {
                throw new BusinessException("Aadhaar number is already registered by another customer");
            }
            profile.setAadhaarNumber(encryptedAadhaar);
        }
        if (request.getMobileNumber() != null) {
            validationService.validateMobile(request.getMobileNumber());
        }
        if (request.getEmail() != null) {
            validationService.validateEmail(request.getEmail());
        }

        // Apply partial update mapping using MapStruct
        customerMapper.updateEntityFromRequest(request, profile);
        profile.setUpdatedBy(currentUserEmail);

        // Recompute status
        ProfileCompletionResponse completion = computeCompletionMetrics(profile);
        profile.setProfileStatus(completion.status());

        CustomerProfile savedProfile = customerRepository.save(profile);
        String decryptedAadhaar = encryptionService.decrypt(savedProfile.getAadhaarNumber());

        return mapToResponse(savedProfile, decryptedAadhaar);
    }

    /**
     * Analyzes customer profile progress to return completion metrics.
     */
    @Transactional(readOnly = true)
    public ProfileCompletionResponse getProfileCompletion(Long userId) {
        log.info("Calculating profile completion metrics for user ID: {}", userId);
        Optional<CustomerProfile> profileOpt = customerRepository.findByUserId(userId);

        if (profileOpt.isEmpty()) {
            return new ProfileCompletionResponse(0, ProfileStatus.INCOMPLETE, getRequiredFieldsList());
        }

        return computeCompletionMetrics(profileOpt.get());
    }

    private CustomerProfileResponse saveProfile(CustomerProfile profile, CustomerProfileRequest request, String currentUserEmail) {
        // Validate format for all incoming fields
        validationService.validateAge(request.getDateOfBirth());
        validationService.validatePan(request.getPanNumber());
        validationService.validateAadhaar(request.getAadhaarNumber());
        validationService.validateMobile(request.getMobileNumber());
        validationService.validateEmail(request.getEmail());

        String incomingPan = request.getPanNumber().trim();
        String incomingAadhaar = request.getAadhaarNumber().trim();

        // Check PAN uniqueness
        Optional<CustomerProfile> existingPan = customerRepository.findByPanNumber(incomingPan);
        if (existingPan.isPresent() && !existingPan.get().getUserId().equals(profile.getUserId())) {
            throw new BusinessException("PAN number is already registered by another customer");
        }

        // Check Aadhaar uniqueness
        String encryptedAadhaar = encryptionService.encrypt(incomingAadhaar);
        Optional<CustomerProfile> existingAadhaar = customerRepository.findByAadhaarNumber(encryptedAadhaar);
        if (existingAadhaar.isPresent() && !existingAadhaar.get().getUserId().equals(profile.getUserId())) {
            throw new BusinessException("Aadhaar number is already registered by another customer");
        }

        // Apply property conversion using MapStruct
        customerMapper.updateEntityFromRequest(request, profile);
        profile.setAadhaarNumber(encryptedAadhaar); // set explicitly since it's encrypted

        // Set status
        ProfileCompletionResponse completion = computeCompletionMetrics(profile);
        profile.setProfileStatus(completion.status());

        CustomerProfile savedProfile = customerRepository.save(profile);
        return mapToResponse(savedProfile, incomingAadhaar);
    }

    private ProfileCompletionResponse computeCompletionMetrics(CustomerProfile profile) {
        List<String> missingFields = new ArrayList<>();
        int filled = 0;
        int total = 15;

        if (isNotEmpty(profile.getFirstName())) { filled++; } else { missingFields.add("firstName"); }
        if (isNotEmpty(profile.getLastName())) { filled++; } else { missingFields.add("lastName"); }
        if (profile.getDateOfBirth() != null) { filled++; } else { missingFields.add("dateOfBirth"); }
        if (isNotEmpty(profile.getGender())) { filled++; } else { missingFields.add("gender"); }
        if (isNotEmpty(profile.getFatherName())) { filled++; } else { missingFields.add("fatherName"); }
        if (isNotEmpty(profile.getMobileNumber())) { filled++; } else { missingFields.add("mobileNumber"); }
        if (isNotEmpty(profile.getEmail())) { filled++; } else { missingFields.add("email"); }
        if (isNotEmpty(profile.getPanNumber())) { filled++; } else { missingFields.add("panNumber"); }
        if (isNotEmpty(profile.getAadhaarNumber())) { filled++; } else { missingFields.add("aadhaarNumber"); }
        if (isNotEmpty(profile.getStreet())) { filled++; } else { missingFields.add("street"); }
        if (isNotEmpty(profile.getCity())) { filled++; } else { missingFields.add("city"); }
        if (isNotEmpty(profile.getState())) { filled++; } else { missingFields.add("state"); }
        if (isNotEmpty(profile.getPincode())) { filled++; } else { missingFields.add("pincode"); }
        if (isNotEmpty(profile.getEmploymentType())) { filled++; } else { missingFields.add("employmentType"); }
        if (profile.getMonthlyIncome() != null) { filled++; } else { missingFields.add("monthlyIncome"); }

        int percentage = (filled * 100) / total;
        ProfileStatus status;

        if (profile.getProfileStatus() == ProfileStatus.VERIFIED) {
            status = ProfileStatus.VERIFIED;
        } else if (percentage == 100) {
            status = ProfileStatus.COMPLETE;
        } else if (percentage > 0) {
            status = ProfileStatus.PARTIAL;
        } else {
            status = ProfileStatus.INCOMPLETE;
        }

        return new ProfileCompletionResponse(percentage, status, missingFields);
    }

    private List<String> getRequiredFieldsList() {
        return List.of(
                "firstName", "lastName", "dateOfBirth", "gender", "fatherName",
                "mobileNumber", "email", "panNumber", "aadhaarNumber",
                "street", "city", "state", "pincode", "employmentType", "monthlyIncome"
        );
    }

    private boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private CustomerProfileResponse mapToResponse(CustomerProfile profile, String rawAadhaar) {
        return CustomerProfileResponse.builder()
                .customerId(profile.getCustomerId())
                .userId(profile.getUserId())
                .firstName(profile.getFirstName())
                .lastName(profile.getLastName())
                .dateOfBirth(profile.getDateOfBirth())
                .gender(profile.getGender())
                .fatherName(profile.getFatherName())
                .mobileNumber(profile.getMobileNumber())
                .email(profile.getEmail())
                .panNumber(encryptionService.maskPan(profile.getPanNumber()))
                .aadhaarNumber(encryptionService.maskAadhaar(rawAadhaar))
                .street(profile.getStreet())
                .city(profile.getCity())
                .state(profile.getState())
                .pincode(profile.getPincode())
                .employmentType(profile.getEmploymentType())
                .companyName(profile.getCompanyName())
                .monthlyIncome(profile.getMonthlyIncome())
                .profileStatus(profile.getProfileStatus().name())
                .build();
    }
}
