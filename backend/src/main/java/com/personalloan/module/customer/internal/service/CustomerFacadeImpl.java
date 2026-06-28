package com.personalloan.module.customer.internal.service;

import com.personalloan.module.customer.api.CustomerFacade;
import com.personalloan.module.customer.api.dto.*;
import com.personalloan.module.customer.internal.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerFacadeImpl implements CustomerFacade {

    private final CustomerRepository customerRepository;
    private final EncryptionService encryptionService;
    private final CustomerService customerService;

    @Override
    @Transactional(readOnly = true)
    public Optional<CustomerSummary> getCustomerSummary(Long userId) {
        return customerRepository.findByUserId(userId)
                .map(profile -> {
                    // Calculate dynamic completion metrics to verify fields presence
                    ProfileCompletionResponse completion = getProfileCompletion(userId);
                    return new CustomerSummary(
                            profile.getCustomerId(),
                            profile.getUserId(),
                            profile.getFirstName(),
                            profile.getLastName(),
                            profile.getEmail(),
                            encryptionService.maskPan(profile.getPanNumber()),
                            profile.getMonthlyIncome(),
                            profile.getEmploymentType(),
                            profile.getProfileStatus(),
                            completion.percentage(),
                            profile.getProfileStatus() == ProfileStatus.VERIFIED
                    );
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CustomerSummary> getCustomerSummaryByCustomerId(Long customerId) {
        return customerRepository.findById(customerId)
                .map(profile -> {
                    ProfileCompletionResponse completion = getProfileCompletion(profile.getUserId());
                    return new CustomerSummary(
                            profile.getCustomerId(),
                            profile.getUserId(),
                            profile.getFirstName(),
                            profile.getLastName(),
                            profile.getEmail(),
                            encryptionService.maskPan(profile.getPanNumber()),
                            profile.getMonthlyIncome(),
                            profile.getEmploymentType(),
                            profile.getProfileStatus(),
                            completion.percentage(),
                            profile.getProfileStatus() == ProfileStatus.VERIFIED
                    );
                });
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerProfileResponse getProfile(Long userId) {
        return customerService.getProfile(userId);
    }

    @Override
    @Transactional
    public CustomerProfileResponse createProfile(Long userId, CustomerProfileRequest request, String currentUserEmail) {
        return customerService.createProfile(userId, request, currentUserEmail);
    }

    @Override
    @Transactional
    public CustomerProfileResponse updateProfile(Long userId, CustomerProfileRequest request, String currentUserEmail) {
        return customerService.updateProfile(userId, request, currentUserEmail);
    }

    @Override
    @Transactional
    public CustomerProfileResponse patchProfile(Long userId, CustomerProfileRequest request, String currentUserEmail) {
        return customerService.patchProfile(userId, request, currentUserEmail);
    }

    @Override
    @Transactional(readOnly = true)
    public ProfileCompletionResponse getProfileCompletion(Long userId) {
        return customerService.getProfileCompletion(userId);
    }
}
