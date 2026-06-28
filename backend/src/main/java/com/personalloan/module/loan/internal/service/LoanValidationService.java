package com.personalloan.module.loan.internal.service;

import com.personalloan.common.exception.BusinessException;
import com.personalloan.module.loan.api.dto.LoanStatus;
import com.personalloan.module.loan.internal.entity.LoanType;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class LoanValidationService {

    /**
     * Asserts that the requested loan amount and tenure fit within the constraints of the loan product.
     */
    public void validateApplicationLimits(LoanType loanType, BigDecimal amount, int tenureMonths) {
        if (loanType == null) {
            throw new BusinessException("Loan configuration type is required");
        }

        if (amount == null || amount.compareTo(loanType.getMinAmount()) < 0 || amount.compareTo(loanType.getMaxAmount()) > 0) {
            throw new BusinessException(String.format("Loan amount must be between %s and %s",
                    loanType.getMinAmount(), loanType.getMaxAmount()));
        }

        if (tenureMonths < loanType.getMinTenureMonths() || tenureMonths > loanType.getMaxTenureMonths()) {
            throw new BusinessException(String.format("Loan tenure must be between %d and %d months",
                    loanType.getMinTenureMonths(), loanType.getMaxTenureMonths()));
        }
    }

    /**
     * Validates status transitions to prevent incorrect flow edits (e.g. CLOSED cannot be reopened).
     */
    public void validateStatusTransition(LoanStatus current, LoanStatus target) {
        if (current == target) {
            return;
        }

        boolean valid = false;
        switch (current) {
            case DRAFT:
                valid = (target == LoanStatus.SUBMITTED || target == LoanStatus.CANCELLED);
                break;
            case SUBMITTED:
                valid = (target == LoanStatus.VERIFIED || target == LoanStatus.REJECTED || target == LoanStatus.CANCELLED);
                break;
            case VERIFIED:
                valid = (target == LoanStatus.UNDER_REVIEW || target == LoanStatus.REJECTED || target == LoanStatus.CANCELLED);
                break;
            case UNDER_REVIEW:
                valid = (target == LoanStatus.APPROVED || target == LoanStatus.REJECTED || target == LoanStatus.CANCELLED);
                break;
            case APPROVED:
                valid = (target == LoanStatus.SANCTIONED || target == LoanStatus.CANCELLED);
                break;
            case SANCTIONED:
                valid = (target == LoanStatus.DISBURSED || target == LoanStatus.CANCELLED);
                break;
            case DISBURSED:
                valid = (target == LoanStatus.CLOSED);
                break;
            case CANCELLED:
            case CLOSED:
            default:
                valid = false;
                break;
        }

        if (!valid) {
            throw new BusinessException("Invalid loan status transition from " + current + " to " + target);
        }
    }
}
