package com.personalloan.module.loan.internal.service;

import com.personalloan.common.exception.BusinessException;
import com.personalloan.module.loan.api.dto.LoanStatus;
import org.springframework.stereotype.Service;

@Service
public class LoanStatusTransitionService {

    /**
     * Validates that the requested status change is legal under the Maker-Checker workflow model.
     * Throws BusinessException if illegal.
     *
     * @param current the current application status
     * @param target the target application status
     */
    public void validateTransition(LoanStatus current, LoanStatus target) {
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
