package com.personalloan.module.loan.internal.service;

import com.personalloan.common.exception.BusinessException;
import com.personalloan.module.customer.api.CustomerFacade;
import com.personalloan.module.customer.api.dto.CustomerSummary;
import com.personalloan.module.customer.api.dto.ProfileStatus;
import com.personalloan.module.loan.api.dto.LoanStatus;
import com.personalloan.module.loan.internal.entity.LoanApplication;
import com.personalloan.module.loan.internal.repository.LoanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoanEligibilityService {

    private final CustomerFacade customerFacade;
    private final LoanRepository loanRepository;

    /**
     * Asserts customer eligibility rules: ProfileStatus checks, One Active Loan constraints, and FOIR (50% threshold).
     *
     * @param customerId the customer ID (profile ID)
     * @param monthlyIncome the monthly income claimed in request
     * @param existingEmis the existing monthly obligations
     * @param proposedEmi the computed proposed EMI of the loan
     */
    public void checkEligibility(Long customerId, BigDecimal monthlyIncome, BigDecimal existingEmis, BigDecimal proposedEmi) {
        log.info("Verifying eligibility metrics for customer ID: {}", customerId);

        // 1. Fetch Customer Summary via CustomerFacade
        CustomerSummary customer = customerFacade.getCustomerSummaryByCustomerId(customerId)
                .orElseThrow(() -> new BusinessException("Customer profile not found. Complete profile creation first."));

        // Assert profile status is COMPLETE or VERIFIED
        if (customer.profileStatus() != ProfileStatus.COMPLETE && customer.profileStatus() != ProfileStatus.VERIFIED) {
            throw new BusinessException("Customer profile is incomplete. Profile status must be COMPLETE or VERIFIED to apply for a loan.");
        }

        // 2. Validate "One Active Loan Rule"
        List<LoanApplication> customerApplications = loanRepository.findByCustomerId(customerId);
        boolean hasActiveLoan = customerApplications.stream()
                .anyMatch(app -> app.getLoanStatus() == LoanStatus.SUBMITTED ||
                                 app.getLoanStatus() == LoanStatus.VERIFIED ||
                                 app.getLoanStatus() == LoanStatus.UNDER_REVIEW ||
                                 app.getLoanStatus() == LoanStatus.APPROVED ||
                                 app.getLoanStatus() == LoanStatus.SANCTIONED ||
                                 app.getLoanStatus() == LoanStatus.DISBURSED);

        if (hasActiveLoan) {
            throw new BusinessException("Customer already has an active loan application. Only one active loan is allowed.");
        }

        // 3. Verify FOIR (Fixed Obligation to Income Ratio) <= 50%
        BigDecimal proposedObligation = proposedEmi == null ? BigDecimal.ZERO : proposedEmi;
        BigDecimal existingObligation = existingEmis == null ? BigDecimal.ZERO : existingEmis;
        BigDecimal totalObligation = proposedObligation.add(existingObligation);

        BigDecimal income = monthlyIncome == null ? BigDecimal.ZERO : monthlyIncome;
        if (income.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Monthly income must be greater than zero");
        }

        BigDecimal foirLimit = income.multiply(BigDecimal.valueOf(0.50));
        if (totalObligation.compareTo(foirLimit) > 0) {
            throw new BusinessException(String.format("Proposed EMI (%s) and existing obligations (%s) exceed the 50%% FOIR threshold of monthly income (%s)",
                    proposedObligation, existingObligation, foirLimit));
        }

        log.info("Eligibility check passed for customer ID: {}", customerId);
    }
}
