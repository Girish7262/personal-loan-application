package com.personalloan.module.loan.internal.service;

import com.personalloan.common.exception.BusinessException;
import com.personalloan.common.exception.ResourceNotFoundException;
import com.personalloan.module.customer.api.CustomerFacade;
import com.personalloan.module.customer.api.dto.CustomerSummary;
import com.personalloan.module.loan.api.dto.*;
import com.personalloan.module.loan.internal.entity.LoanApplication;
import com.personalloan.module.loan.internal.entity.LoanType;
import com.personalloan.module.loan.internal.repository.LoanRepository;
import com.personalloan.module.loan.internal.repository.LoanTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoanService {

    private final LoanRepository loanRepository;
    private final LoanTypeRepository loanTypeRepository;
    private final CustomerFacade customerFacade;
    private final EmiCalculationService emiCalculationService;
    private final LoanValidationService loanValidationService;
    private final LoanEligibilityService loanEligibilityService;

    /**
     * Submits a new loan application. Calculates EMI, performs eligibility checks,
     * seeds initial status as SUBMITTED, and generates a unique application number.
     *
     * @param userId the user ID owning the application
     * @param request the application request parameters
     * @param currentUserEmail the active user's email
     * @return the saved application response details
     */
    @Transactional
    public LoanApplicationResponse submitApplication(Long userId, LoanApplicationRequest request, String currentUserEmail) {
        log.info("Submitting loan application for user ID: {}", userId);

        // 1. Resolve CustomerProfile ID via CustomerFacade
        CustomerSummary customer = customerFacade.getCustomerSummary(userId)
                .orElseThrow(() -> new BusinessException("Customer profile not found. Complete profile creation first."));

        // 2. Resolve Loan Configuration Type
        LoanType loanType = loanTypeRepository.findById(request.getLoanTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Requested loan type configuration not found"));

        if (!loanType.getIsActive()) {
            throw new BusinessException("Requested loan type is currently inactive");
        }

        // 3. Validate product bounds (amount and tenure)
        loanValidationService.validateApplicationLimits(loanType, request.getLoanAmount(), request.getLoanTenureMonths());

        // 4. Calculate proposed monthly EMI
        BigDecimal baseEmi = emiCalculationService.calculateEmi(
                request.getLoanAmount(),
                loanType.getBaseInterestRate(),
                request.getLoanTenureMonths()
        );

        // 5. Assert customer eligibility (Profile status, FOIR, active loan checks)
        loanEligibilityService.checkEligibility(
                customer.customerId(),
                request.getMonthlyIncome(),
                request.getExistingEmis(),
                baseEmi
        );

        // 6. Assemble LoanApplication entity
        String appNum = "LN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        LoanApplication application = LoanApplication.builder()
                .customerId(customer.customerId())
                .loanType(loanType)
                .applicationNumber(appNum)
                .loanStatus(LoanStatus.SUBMITTED)
                .loanAmount(request.getLoanAmount())
                .loanTenureMonths(request.getLoanTenureMonths())
                .interestRate(loanType.getBaseInterestRate()) // Preserve base interest rate from configs
                .purpose(request.getPurpose())
                .monthlyIncome(request.getMonthlyIncome())
                .existingEmis(request.getExistingEmis())
                .emi(baseEmi)
                .submittedAt(LocalDateTime.now())
                .createdBy(currentUserEmail)
                .isDeleted(false)
                .build();

        LoanApplication saved = loanRepository.save(application);
        log.info("Loan application successfully submitted. Loan ID: {}, App Number: {}", saved.getLoanId(), appNum);

        return mapToResponse(saved);
    }

    /**
     * Retrieves all loan applications submitted by the customer associated with the user ID.
     */
    @Transactional(readOnly = true)
    public List<LoanApplicationResponse> getApplications(Long userId) {
        CustomerSummary customer = customerFacade.getCustomerSummary(userId)
                .orElseThrow(() -> new BusinessException("Customer profile not found"));

        return loanRepository.findByCustomerId(customer.customerId()).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves specific application details. Asserts owner security bounds.
     */
    @Transactional(readOnly = true)
    public LoanApplicationResponse getApplication(Long loanId, Long userId) {
        CustomerSummary customer = customerFacade.getCustomerSummary(userId)
                .orElseThrow(() -> new BusinessException("Customer profile not found"));

        LoanApplication application = loanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan application not found"));

        if (!application.getCustomerId().equals(customer.customerId())) {
            throw new BusinessException("Access denied. You do not own this application.");
        }

        return mapToResponse(application);
    }

    /**
     * Updates/transitions status. Logs specific timestamps for events (approvedAt, disbursedAt).
     */
    @Transactional
    public LoanApplicationResponse updateApplicationStatus(Long loanId, LoanStatus targetStatus, String currentUserEmail) {
        log.info("Transitioning loan status for loan ID: {} to {}", loanId, targetStatus);
        LoanApplication application = loanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan application not found"));

        // Validate status transition
        loanValidationService.validateStatusTransition(application.getLoanStatus(), targetStatus);

        application.setLoanStatus(targetStatus);
        application.setUpdatedBy(currentUserEmail);

        // Append timestamp markers
        if (targetStatus == LoanStatus.APPROVED) {
            application.setApprovedAt(LocalDateTime.now());
            application.setApprovedAmount(application.getLoanAmount()); // Default approved amount to applied amount
        } else if (targetStatus == LoanStatus.DISBURSED) {
            application.setDisbursedAt(LocalDateTime.now());
        }

        LoanApplication saved = loanRepository.save(application);
        return mapToResponse(saved);
    }

    private LoanApplicationResponse mapToResponse(LoanApplication app) {
        LoanTypeResponse typeResponse = LoanTypeResponse.builder()
                .loanTypeId(app.getLoanType().getLoanTypeId())
                .name(app.getLoanType().getName())
                .description(app.getLoanType().getDescription())
                .minAmount(app.getLoanType().getMinAmount())
                .maxAmount(app.getLoanType().getMaxAmount())
                .minTenureMonths(app.getLoanType().getMinTenureMonths())
                .maxTenureMonths(app.getLoanType().getMaxTenureMonths())
                .baseInterestRate(app.getLoanType().getBaseInterestRate())
                .isActive(app.getLoanType().getIsActive())
                .build();

        return LoanApplicationResponse.builder()
                .loanId(app.getLoanId())
                .customerId(app.getCustomerId())
                .loanType(typeResponse)
                .applicationNumber(app.getApplicationNumber())
                .loanStatus(app.getLoanStatus().name())
                .loanAmount(app.getLoanAmount())
                .approvedAmount(app.getApprovedAmount())
                .loanTenureMonths(app.getLoanTenureMonths())
                .interestRate(app.getInterestRate())
                .purpose(app.getPurpose().name())
                .monthlyIncome(app.getMonthlyIncome())
                .existingEmis(app.getExistingEmis())
                .emi(app.getEmi())
                .createdAt(app.getCreatedAt())
                .updatedAt(app.getUpdatedAt())
                .createdBy(app.getCreatedBy())
                .updatedBy(app.getUpdatedBy())
                .version(app.getVersion())
                .build();
    }
}
