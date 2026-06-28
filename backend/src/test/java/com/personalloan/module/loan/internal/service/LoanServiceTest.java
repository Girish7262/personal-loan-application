package com.personalloan.module.loan.internal.service;

import com.personalloan.common.exception.BusinessException;
import com.personalloan.module.customer.api.CustomerFacade;
import com.personalloan.module.customer.api.dto.CustomerSummary;
import com.personalloan.module.customer.api.dto.ProfileStatus;
import com.personalloan.module.loan.api.dto.*;
import com.personalloan.module.loan.event.LoanStatusChangedEvent;
import com.personalloan.module.loan.internal.entity.LoanApplication;
import com.personalloan.module.loan.internal.entity.LoanType;
import com.personalloan.module.loan.internal.repository.LoanRepository;
import com.personalloan.module.loan.internal.repository.LoanTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanServiceTest {

    @Mock private LoanRepository loanRepository;
    @Mock private LoanTypeRepository loanTypeRepository;
    @Mock private CustomerFacade customerFacade;
    @Mock private EmiCalculationService emiCalculationService;
    @Mock private LoanValidationService loanValidationService;
    @Mock private LoanEligibilityService loanEligibilityService;
    @Mock private LoanStatusTransitionService loanStatusTransitionService;
    @Mock private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private LoanService loanService;

    private Long userId;
    private CustomerSummary customerSummary;
    private LoanType loanType;
    private LoanApplicationRequest request;
    private LoanApplication application;

    @BeforeEach
    void setUp() {
        userId = 100L;
        customerSummary = new CustomerSummary(10L, userId, "Girish", "Patil", "girish@gmail.com", "XXXXXX1234",
                BigDecimal.valueOf(100000), "SALARIED", ProfileStatus.COMPLETE, 100, false);

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

        request = LoanApplicationRequest.builder()
                .loanTypeId(1L)
                .loanAmount(BigDecimal.valueOf(100000))
                .loanTenureMonths(12)
                .purpose(LoanPurpose.MEDICAL)
                .monthlyIncome(BigDecimal.valueOf(100000))
                .existingEmis(BigDecimal.valueOf(10000))
                .build();

        application = LoanApplication.builder()
                .loanId(1L)
                .customerId(10L)
                .loanType(loanType)
                .applicationNumber("PL-2026-00000001")
                .loanStatus(LoanStatus.SUBMITTED)
                .loanAmount(BigDecimal.valueOf(100000))
                .loanTenureMonths(12)
                .interestRate(BigDecimal.valueOf(12.00))
                .purpose(LoanPurpose.MEDICAL)
                .monthlyIncome(BigDecimal.valueOf(100000))
                .existingEmis(BigDecimal.valueOf(10000))
                .emi(BigDecimal.valueOf(8884.88))
                .build();
    }

    @Test
    void submitApplication_WhenEligible_ShouldGenerateSequentialNumberAndPublishEvent() {
        // Arrange
        when(customerFacade.getCustomerSummary(userId)).thenReturn(Optional.of(customerSummary));
        when(loanTypeRepository.findById(request.getLoanTypeId())).thenReturn(Optional.of(loanType));
        when(emiCalculationService.calculateEmi(any(), any(), anyInt())).thenReturn(BigDecimal.valueOf(8884.88));
        
        EligibilityResult eligibility = new EligibilityResult(true, Collections.emptyList(), BigDecimal.valueOf(500000), BigDecimal.valueOf(18.00));
        when(loanEligibilityService.evaluateEligibility(any(), any(), any(), any(), any())).thenReturn(eligibility);

        // Sequence generator mock
        int year = LocalDate.now().getYear();
        String prefix = "PL-" + year + "-";
        when(loanRepository.findFirstByApplicationNumberStartingWithOrderByApplicationNumberDesc(prefix))
                .thenReturn(Optional.empty());

        when(loanRepository.save(any(LoanApplication.class))).thenReturn(application);

        // Act
        LoanApplicationResponse response = loanService.submitApplication(userId, request, "girish@gmail.com");

        // Assert
        assertNotNull(response);
        assertEquals("PL-2026-00000001", response.getApplicationNumber());
        verify(loanValidationService, times(1)).validateApplicationLimits(eq(loanType), any(), anyInt());
        verify(loanRepository, times(1)).save(any(LoanApplication.class));
        verify(eventPublisher, times(1)).publishEvent(any(LoanStatusChangedEvent.class));
    }

    @Test
    void submitApplication_WhenNotEligible_ShouldThrowBusinessException() {
        // Arrange
        when(customerFacade.getCustomerSummary(userId)).thenReturn(Optional.of(customerSummary));
        when(loanTypeRepository.findById(request.getLoanTypeId())).thenReturn(Optional.of(loanType));
        
        EligibilityResult eligibility = new EligibilityResult(false, Collections.singletonList("FOIR threshold exceeded"), BigDecimal.ZERO, BigDecimal.valueOf(65.00));
        when(loanEligibilityService.evaluateEligibility(any(), any(), any(), any(), any())).thenReturn(eligibility);

        // Act & Assert
        assertThrows(BusinessException.class, () -> 
                loanService.submitApplication(userId, request, "girish@gmail.com"));
        verify(loanRepository, never()).save(any(LoanApplication.class));
    }

    @Test
    void updateApplicationStatus_ShouldValidateTransitionAndPublishStatusChangedEvent() {
        // Arrange
        when(loanRepository.findById(1L)).thenReturn(Optional.of(application));
        when(loanRepository.save(application)).thenReturn(application);

        // Act
        LoanApplicationResponse response = loanService.updateApplicationStatus(1L, LoanStatus.APPROVED, 100L, "officer@gmail.com");

        // Assert
        assertNotNull(response);
        verify(loanStatusTransitionService, times(1)).validateTransition(LoanStatus.SUBMITTED, LoanStatus.APPROVED);
        verify(loanRepository, times(1)).save(application);
        verify(eventPublisher, times(1)).publishEvent(any(LoanStatusChangedEvent.class));
        assertNotNull(application.getApprovedAt());
    }
}
