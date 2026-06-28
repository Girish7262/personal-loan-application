package com.personalloan.module.loan.internal.service;

import com.personalloan.common.exception.BusinessException;
import com.personalloan.common.exception.ResourceNotFoundException;
import com.personalloan.module.customer.api.CustomerFacade;
import com.personalloan.module.customer.api.dto.CustomerSummary;
import com.personalloan.module.loan.api.dto.*;
import com.personalloan.module.loan.event.LoanStatusChangedEvent;
import com.personalloan.module.loan.internal.entity.LoanApplication;
import com.personalloan.module.loan.internal.entity.LoanType;
import com.personalloan.module.loan.internal.repository.LoanRepository;
import com.personalloan.module.loan.internal.repository.LoanTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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
    private final LoanStatusTransitionService loanStatusTransitionService;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * Pre-evaluates the eligibility for a proposed loan application.
     */
    @Transactional(readOnly = true)
    public EligibilityResult checkEligibility(Long userId, LoanApplicationRequest request) {
        CustomerSummary customer = customerFacade.getCustomerSummary(userId)
                .orElseThrow(() -> new BusinessException("Customer profile not found. Complete profile creation first."));

        LoanType loanType = loanTypeRepository.findById(request.getLoanTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Requested loan type configuration not found"));

        BigDecimal proposedEmi = emiCalculationService.calculateEmi(
                request.getLoanAmount(),
                loanType.getBaseInterestRate(),
                request.getLoanTenureMonths()
        );

        return loanEligibilityService.evaluateEligibility(
                customer.customerId(),
                request.getMonthlyIncome(),
                request.getExistingEmis(),
                proposedEmi,
                loanType
        );
    }

    /**
     * Submits a new loan application. Calculates EMI, performs eligibility checks,
     * seeds initial status as SUBMITTED, and generates a database-safe sequential loan number.
     */
    @Transactional
    public LoanApplicationResponse submitApplication(Long userId, LoanApplicationRequest request, String currentUserEmail) {
        log.info("Submitting loan application for user ID: {}", userId);

        CustomerSummary customer = customerFacade.getCustomerSummary(userId)
                .orElseThrow(() -> new BusinessException("Customer profile not found. Complete profile creation first."));

        LoanType loanType = loanTypeRepository.findById(request.getLoanTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Requested loan type configuration not found"));

        if (!loanType.getIsActive()) {
            throw new BusinessException("Requested loan type is currently inactive");
        }

        // 1. Validate limits
        loanValidationService.validateApplicationLimits(loanType, request.getLoanAmount(), request.getLoanTenureMonths());

        // 2. Compute EMI
        BigDecimal baseEmi = emiCalculationService.calculateEmi(
                request.getLoanAmount(),
                loanType.getBaseInterestRate(),
                request.getLoanTenureMonths()
        );

        // 3. Evaluate eligibility
        EligibilityResult eligibility = loanEligibilityService.evaluateEligibility(
                customer.customerId(),
                request.getMonthlyIncome(),
                request.getExistingEmis(),
                baseEmi,
                loanType
        );

        if (!eligibility.eligible()) {
            String firstReason = eligibility.reasons().isEmpty() ? "Eligibility validation failed" : eligibility.reasons().get(0);
            throw new BusinessException(firstReason);
        }

        // 4. Generate sequential application number PL-YYYY-00000001
        String appNum = generateNextApplicationNumber();

        // 5. Build entity
        LoanApplication application = LoanApplication.builder()
                .customerId(customer.customerId())
                .loanType(loanType)
                .applicationNumber(appNum)
                .loanStatus(LoanStatus.SUBMITTED)
                .loanAmount(request.getLoanAmount())
                .loanTenureMonths(request.getLoanTenureMonths())
                .interestRate(loanType.getBaseInterestRate())
                .purpose(request.getPurpose())
                .monthlyIncome(request.getMonthlyIncome())
                .existingEmis(request.getExistingEmis())
                .emi(baseEmi)
                .submittedAt(LocalDateTime.now())
                .createdBy(currentUserEmail)
                .isDeleted(false)
                .build();

        LoanApplication saved = loanRepository.save(application);

        // 6. Trigger domain event to log status history (from null -> SUBMITTED)
        eventPublisher.publishEvent(new LoanStatusChangedEvent(
                this,
                saved.getLoanId(),
                null,
                LoanStatus.SUBMITTED,
                userId,
                "Loan application submitted"
        ));

        log.info("Loan application submitted successfully. Loan ID: {}, App Number: {}", saved.getLoanId(), appNum);
        return mapToResponse(saved);
    }

    /**
     * Generates a sequential loan number database-safely.
     */
    private synchronized String generateNextApplicationNumber() {
        int year = LocalDate.now().getYear();
        String prefix = "PL-" + year + "-";
        Optional<LoanApplication> lastApp = loanRepository
                .findFirstByApplicationNumberStartingWithOrderByApplicationNumberDesc(prefix);

        int suffixNum = 1;
        if (lastApp.isPresent()) {
            String lastNum = lastApp.get().getApplicationNumber();
            try {
                String suffixPart = lastNum.substring(prefix.length());
                suffixNum = Integer.parseInt(suffixPart) + 1;
            } catch (Exception e) {
                log.warn("Failed to parse sequential suffix from loan number: {}. Suffix reset to 1", lastNum, e);
            }
        }
        return String.format("%s%08d", prefix, suffixNum);
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
    public LoanApplicationResponse updateApplicationStatus(Long loanId, LoanStatus targetStatus, Long actorUserId, String currentUserEmail) {
        return updateApplicationStatus(loanId, targetStatus, null, null, actorUserId, currentUserEmail);
    }

    /**
     * Updates/transitions status, allowing overriding loan amounts and interest rates.
     */
    @Transactional
    public LoanApplicationResponse updateApplicationStatus(
            Long loanId,
            LoanStatus targetStatus,
            BigDecimal approvedAmount,
            BigDecimal interestRate,
            Long actorUserId,
            String currentUserEmail) {

        log.info("Transitioning loan status for loan ID: {} to {} with approvedAmount: {}, interestRate: {}", 
                loanId, targetStatus, approvedAmount, interestRate);
        
        LoanApplication application = loanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan application not found"));

        LoanStatus oldStatus = application.getLoanStatus();

        // Validate status transition via the transition state machine service
        loanStatusTransitionService.validateTransition(oldStatus, targetStatus);

        application.setLoanStatus(targetStatus);
        application.setUpdatedBy(currentUserEmail);

        // Apply overrides if provided
        if (approvedAmount != null) {
            application.setApprovedAmount(approvedAmount);
        }
        if (interestRate != null) {
            application.setInterestRate(interestRate);
        }

        // Append timestamp markers
        if (targetStatus == LoanStatus.APPROVED) {
            application.setApprovedAt(LocalDateTime.now());
            if (application.getApprovedAmount() == null) {
                application.setApprovedAmount(application.getLoanAmount());
            }
        } else if (targetStatus == LoanStatus.DISBURSED) {
            application.setDisbursedAt(LocalDateTime.now());
        }

        LoanApplication saved = loanRepository.save(application);

        // Trigger domain event for status logging
        eventPublisher.publishEvent(new LoanStatusChangedEvent(
                this,
                saved.getLoanId(),
                oldStatus,
                targetStatus,
                actorUserId,
                "Status transitioned from " + oldStatus + " to " + targetStatus
        ));

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
                .foirPercentage(app.getLoanType().getFoirPercentage())
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
