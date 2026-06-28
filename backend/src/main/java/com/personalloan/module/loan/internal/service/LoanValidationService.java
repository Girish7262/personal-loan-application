package com.personalloan.module.loan.internal.service;

import com.personalloan.common.exception.BusinessException;
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
}
