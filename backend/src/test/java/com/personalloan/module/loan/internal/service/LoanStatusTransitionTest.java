package com.personalloan.module.loan.internal.service;

import com.personalloan.common.exception.BusinessException;
import com.personalloan.module.loan.api.dto.LoanStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LoanStatusTransitionTest {

    private final LoanStatusTransitionService statusTransitionService = new LoanStatusTransitionService();

    @Test
    void validateTransition_WithLegalTransitions_ShouldNotThrowException() {
        assertDoesNotThrow(() -> statusTransitionService.validateTransition(LoanStatus.DRAFT, LoanStatus.SUBMITTED));
        assertDoesNotThrow(() -> statusTransitionService.validateTransition(LoanStatus.SUBMITTED, LoanStatus.VERIFIED));
        assertDoesNotThrow(() -> statusTransitionService.validateTransition(LoanStatus.VERIFIED, LoanStatus.UNDER_REVIEW));
        assertDoesNotThrow(() -> statusTransitionService.validateTransition(LoanStatus.UNDER_REVIEW, LoanStatus.APPROVED));
        assertDoesNotThrow(() -> statusTransitionService.validateTransition(LoanStatus.APPROVED, LoanStatus.SANCTIONED));
        assertDoesNotThrow(() -> statusTransitionService.validateTransition(LoanStatus.SANCTIONED, LoanStatus.DISBURSED));
        assertDoesNotThrow(() -> statusTransitionService.validateTransition(LoanStatus.DISBURSED, LoanStatus.CLOSED));
    }

    @Test
    void validateTransition_WithIllegalTransitions_ShouldThrowBusinessException() {
        assertThrows(BusinessException.class, () -> 
                statusTransitionService.validateTransition(LoanStatus.CLOSED, LoanStatus.SUBMITTED));
        assertThrows(BusinessException.class, () -> 
                statusTransitionService.validateTransition(LoanStatus.DISBURSED, LoanStatus.APPROVED));
        assertThrows(BusinessException.class, () -> 
                statusTransitionService.validateTransition(LoanStatus.SUBMITTED, LoanStatus.SANCTIONED));
    }
}
