package com.personalloan.module.loan.internal.service;

import com.personalloan.common.exception.BusinessException;
import com.personalloan.module.customer.api.CustomerFacade;
import com.personalloan.module.customer.api.dto.CustomerSummary;
import com.personalloan.module.customer.api.dto.ProfileStatus;
import com.personalloan.module.loan.api.dto.EligibilityResult;
import com.personalloan.module.loan.api.dto.LoanStatus;
import com.personalloan.module.loan.internal.entity.LoanApplication;
import com.personalloan.module.loan.internal.entity.LoanType;
import com.personalloan.module.loan.internal.repository.LoanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoanEligibilityService {

    private final CustomerFacade customerFacade;
    private final LoanRepository loanRepository;

    private static final MathContext MC = new MathContext(16, RoundingMode.HALF_UP);

    /**
     * Evaluates all eligibility criteria dynamically and returns a structured EligibilityResult.
     * Asserts customer profile completeness, FOIR thresholds, and active loan constraints.
     */
    public EligibilityResult evaluateEligibility(Long customerId, BigDecimal monthlyIncome, BigDecimal existingEmis, BigDecimal proposedEmi, LoanType loanType) {
        log.info("Evaluating dynamic eligibility metrics for customer ID: {}", customerId);

        List<String> reasons = new ArrayList<>();
        BigDecimal foirPercentageCalculated = BigDecimal.ZERO;
        BigDecimal maxEligibleAmount = BigDecimal.ZERO;

        BigDecimal income = monthlyIncome == null ? BigDecimal.ZERO : monthlyIncome;
        BigDecimal existingObligations = existingEmis == null ? BigDecimal.ZERO : existingEmis;
        BigDecimal newEmi = proposedEmi == null ? BigDecimal.ZERO : proposedEmi;

        // 1. Resolve and check Customer Profile
        Optional<CustomerSummary> customerOpt = customerFacade.getCustomerSummaryByCustomerId(customerId);
        if (customerOpt.isEmpty()) {
            reasons.add("Customer profile not found. Complete onboarding first.");
        } else {
            CustomerSummary customer = customerOpt.get();
            if (customer.profileStatus() != ProfileStatus.COMPLETE && customer.profileStatus() != ProfileStatus.VERIFIED) {
                reasons.add("Customer profile status must be COMPLETE or VERIFIED. Current status: " + customer.profileStatus());
            }
        }

        // 2. Resolve and check active loan limitations (One Active Loan Rule)
        List<LoanApplication> customerApplications = loanRepository.findByCustomerId(customerId);
        boolean hasActiveLoan = customerApplications.stream()
                .anyMatch(app -> app.getLoanStatus() == LoanStatus.SUBMITTED ||
                                 app.getLoanStatus() == LoanStatus.VERIFIED ||
                                 app.getLoanStatus() == LoanStatus.UNDER_REVIEW ||
                                 app.getLoanStatus() == LoanStatus.APPROVED ||
                                 app.getLoanStatus() == LoanStatus.SANCTIONED ||
                                 app.getLoanStatus() == LoanStatus.DISBURSED);

        if (hasActiveLoan) {
            reasons.add("Customer already has an active loan application. Only one active loan is allowed.");
        }

        // 3. Verify FOIR calculations
        BigDecimal foirThreshold = loanType.getFoirPercentage() != null ? loanType.getFoirPercentage() : BigDecimal.valueOf(50.00);
        BigDecimal totalObligations = existingObligations.add(newEmi);

        if (income.compareTo(BigDecimal.ZERO) > 0) {
            foirPercentageCalculated = totalObligations
                    .multiply(BigDecimal.valueOf(100))
                    .divide(income, 2, RoundingMode.HALF_UP);

            if (foirPercentageCalculated.compareTo(foirThreshold) > 0) {
                reasons.add(String.format("Proposed obligations exceed configured FOIR threshold of %s%%. Calculated FOIR: %s%%",
                        foirThreshold, foirPercentageCalculated));
            }
        } else {
            reasons.add("Monthly income must be greater than zero.");
        }

        // 4. Calculate Maximum Eligible Loan Amount based on remaining available FOIR budget
        if (income.compareTo(BigDecimal.ZERO) > 0 && foirThreshold.compareTo(BigDecimal.ZERO) > 0) {
            // maxEmiBudget = income * (foirThreshold / 100)
            BigDecimal maxEmiBudget = income.multiply(foirThreshold).divide(BigDecimal.valueOf(100), MC);
            // maxNewEmi = maxEmiBudget - existingObligations
            BigDecimal maxNewEmi = maxEmiBudget.subtract(existingObligations);

            if (maxNewEmi.compareTo(BigDecimal.ZERO) > 0) {
                // Reverse amortization formula to calculate max principal:
                // P = [EMI * ((1+R)^N - 1)] / [R * (1+R)^N]
                BigDecimal annualRate = loanType.getBaseInterestRate();
                int tenureMonths = loanType.getMaxTenureMonths(); // Calculate limits using maximum tenure

                if (annualRate.compareTo(BigDecimal.ZERO) > 0 && tenureMonths > 0) {
                    BigDecimal r = annualRate.divide(BigDecimal.valueOf(1200), 10, RoundingMode.HALF_UP);
                    BigDecimal onePlusR = BigDecimal.ONE.add(r);
                    BigDecimal onePlusRPowN = onePlusR.pow(tenureMonths, MC);

                    BigDecimal numerator = maxNewEmi.multiply(onePlusRPowN.subtract(BigDecimal.ONE), MC);
                    BigDecimal denominator = r.multiply(onePlusRPowN, MC);

                    if (denominator.compareTo(BigDecimal.ZERO) > 0) {
                        maxEligibleAmount = numerator.divide(denominator, 2, RoundingMode.HALF_UP);
                        // Cap maximum eligible amount to loan type limits
                        if (maxEligibleAmount.compareTo(loanType.getMaxAmount()) > 0) {
                            maxEligibleAmount = loanType.getMaxAmount();
                        }
                    }
                } else if (annualRate.compareTo(BigDecimal.ZERO) == 0 && tenureMonths > 0) {
                    // No interest flat reverse rate
                    maxEligibleAmount = maxNewEmi.multiply(BigDecimal.valueOf(tenureMonths));
                    if (maxEligibleAmount.compareTo(loanType.getMaxAmount()) > 0) {
                        maxEligibleAmount = loanType.getMaxAmount();
                    }
                }
            }
        }

        boolean eligible = reasons.isEmpty();
        log.info("Eligibility evaluation completed. Eligible: {}", eligible);

        return new EligibilityResult(eligible, reasons, maxEligibleAmount, foirPercentageCalculated);
    }
}
