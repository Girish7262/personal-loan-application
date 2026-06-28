package com.personalloan.module.approval.internal.service;

import com.personalloan.common.exception.BusinessException;
import com.personalloan.module.loan.api.dto.LoanStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ApprovalValidationService {

    /**
     * Validates that the transition from currentStatus to targetStatus complies with
     * the formal Maker-Checker credit review sequence workflow.
     */
    public void validateTransition(LoanStatus currentStatus, LoanStatus targetStatus) {
        log.info("Validating workflow transition: {} -> {}", currentStatus, targetStatus);

        boolean valid = false;
        switch (targetStatus) {
            case VERIFIED:
                // Maker verification requires application to be SUBMITTED
                valid = (currentStatus == LoanStatus.SUBMITTED);
                break;
            case APPROVED:
            case REJECTED:
                // Checker approval/rejection requires VERIFIED (or UNDER_REVIEW)
                valid = (currentStatus == LoanStatus.VERIFIED || currentStatus == LoanStatus.UNDER_REVIEW);
                break;
            case SANCTIONED:
                // Finance Sanctioning requires APPROVED
                valid = (currentStatus == LoanStatus.APPROVED);
                break;
            case DISBURSED:
                // Finance Disbursement requires SANCTIONED
                valid = (currentStatus == LoanStatus.SANCTIONED);
                break;
            default:
                // Other transitions are managed by direct Loan application flow
                valid = true;
        }

        if (!valid) {
            log.error("Illegal state transition attempt in approval workflow: {} -> {}", currentStatus, targetStatus);
            throw new BusinessException("Illegal transition in Maker-Checker credit workflow: " + currentStatus + " -> " + targetStatus);
        }
    }

    /**
     * Validates that remarks are present and not empty.
     */
    public void validateRemarksPresent(String remarks, String actionName) {
        if (remarks == null || remarks.trim().isEmpty()) {
            throw new BusinessException("Remarks/comments are mandatory for action: " + actionName);
        }
    }
}
