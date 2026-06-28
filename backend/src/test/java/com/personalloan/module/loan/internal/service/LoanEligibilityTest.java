package com.personalloan.module.loan.internal.service;

import com.personalloan.module.customer.api.CustomerFacade;
import com.personalloan.module.customer.api.dto.CustomerSummary;
import com.personalloan.module.customer.api.dto.ProfileStatus;
import com.personalloan.module.loan.api.dto.EligibilityResult;
import com.personalloan.module.loan.api.dto.LoanStatus;
import com.personalloan.module.loan.internal.entity.LoanApplication;
import com.personalloan.module.loan.internal.entity.LoanType;
import com.personalloan.module.loan.internal.repository.LoanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoanEligibilityTest {

    @Mock
    private CustomerFacade customerFacade;

    @Mock
    private LoanRepository loanRepository;

    @InjectMocks
    private LoanEligibilityService eligibilityService;

    private Long customerId;
    private CustomerSummary customerSummary;
    private LoanType loanType;

    @BeforeEach
    void setUp() {
        customerId = 10L;
        customerSummary = new CustomerSummary(
                customerId,
                100L,
                "Girish",
                "Patil",
                "girish@gmail.com",
                "XXXXXX1234",
                BigDecimal.valueOf(100000),
                "SALARIED",
                ProfileStatus.COMPLETE,
                100,
                false
        );

        loanType = LoanType.builder()
                .loanTypeId(1L)
                .name("Personal Loan")
                .minAmount(BigDecimal.valueOf(10000))
                .maxAmount(BigDecimal.valueOf(500000))
                .minTenureMonths(6)
                .maxTenureMonths(36)
                .baseInterestRate(BigDecimal.valueOf(12.00))
                .foirPercentage(BigDecimal.valueOf(50.00))
                .isActive(true)
                .build();
    }

    @Test
    void evaluateEligibility_WhenUnderFoirLimitAndProfileComplete_ShouldReturnEligible() {
        // Arrange
        when(customerFacade.getCustomerSummaryByCustomerId(customerId)).thenReturn(Optional.of(customerSummary));
        when(loanRepository.findByCustomerId(customerId)).thenReturn(Collections.emptyList());

        BigDecimal monthlyIncome = BigDecimal.valueOf(100000);
        BigDecimal existingEmis = BigDecimal.valueOf(20000);
        BigDecimal proposedEmi = BigDecimal.valueOf(25000); // Total 45,000 <= 50,000 (50% FOIR)

        // Act
        EligibilityResult result = eligibilityService.evaluateEligibility(
                customerId, monthlyIncome, existingEmis, proposedEmi, loanType);

        // Assert
        assertTrue(result.eligible());
        assertTrue(result.reasons().isEmpty());
        assertEquals(BigDecimal.valueOf(45.00).setScale(2), result.foir());
        assertTrue(result.maxEligibleAmount().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    void evaluateEligibility_WhenOverFoirLimit_ShouldReturnNotEligible() {
        // Arrange
        when(customerFacade.getCustomerSummaryByCustomerId(customerId)).thenReturn(Optional.of(customerSummary));
        when(loanRepository.findByCustomerId(customerId)).thenReturn(Collections.emptyList());

        BigDecimal monthlyIncome = BigDecimal.valueOf(100000);
        BigDecimal existingEmis = BigDecimal.valueOf(30000);
        BigDecimal proposedEmi = BigDecimal.valueOf(25000); // Total 55,000 > 50,000 (50% FOIR)

        // Act
        EligibilityResult result = eligibilityService.evaluateEligibility(
                customerId, monthlyIncome, existingEmis, proposedEmi, loanType);

        // Assert
        assertFalse(result.eligible());
        assertFalse(result.reasons().isEmpty());
        assertTrue(result.reasons().get(0).contains("exceed configured FOIR threshold"));
    }

    @Test
    void evaluateEligibility_WhenCustomerAlreadyHasActiveLoan_ShouldReturnNotEligible() {
        // Arrange
        when(customerFacade.getCustomerSummaryByCustomerId(customerId)).thenReturn(Optional.of(customerSummary));
        LoanApplication activeLoan = LoanApplication.builder()
                .loanStatus(LoanStatus.SUBMITTED)
                .build();
        when(loanRepository.findByCustomerId(customerId)).thenReturn(Collections.singletonList(activeLoan));

        // Act
        EligibilityResult result = eligibilityService.evaluateEligibility(
                customerId, BigDecimal.valueOf(100000), BigDecimal.ZERO, BigDecimal.valueOf(5000), loanType);

        // Assert
        assertFalse(result.eligible());
        assertTrue(result.reasons().contains("Customer already has an active loan application. Only one active loan is allowed."));
    }

    @Test
    void evaluateEligibility_WhenCustomerProfileIncomplete_ShouldReturnNotEligible() {
        // Arrange
        CustomerSummary incompleteCustomer = new CustomerSummary(
                customerId, 100L, "Girish", "Patil", "girish@gmail.com", "XXXXXX1234",
                BigDecimal.valueOf(100000), "SALARIED", ProfileStatus.PARTIAL, 50, false);

        when(customerFacade.getCustomerSummaryByCustomerId(customerId)).thenReturn(Optional.of(incompleteCustomer));

        // Act
        EligibilityResult result = eligibilityService.evaluateEligibility(
                customerId, BigDecimal.valueOf(100000), BigDecimal.ZERO, BigDecimal.valueOf(5000), loanType);

        // Assert
        assertFalse(result.eligible());
        assertTrue(result.reasons().get(0).contains("must be COMPLETE or VERIFIED"));
    }
}
