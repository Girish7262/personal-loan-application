package com.personalloan.module.approval.internal.service;

import com.personalloan.module.loan.api.dto.LoanStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreditApprovalService {

    private final ApprovalWorkflowService workflowService;

    /**
     * Executes Checker Approval for a loan application, supporting amount changes and interest overrides.
     */
    public void approveApplication(
            Long loanId,
            Long actorUserId,
            String actorEmail,
            String remarks,
            BigDecimal approvedAmount,
            BigDecimal interestRate) {
        log.info("Credit Approval triggered for Loan ID: {} by User ID: {} with amount: {}", loanId, actorUserId, approvedAmount);
        workflowService.executeWorkflowStep(
                loanId,
                LoanStatus.APPROVED,
                actorUserId,
                actorEmail,
                remarks,
                approvedAmount,
                interestRate
        );
    }

    /**
     * Rejects a loan application. Remarks are mandatory and validated by the workflow engine.
     */
    public void rejectApplication(Long loanId, Long actorUserId, String actorEmail, String remarks) {
        log.info("Credit Rejection triggered for Loan ID: {} by User ID: {}", loanId, actorUserId);
        workflowService.executeWorkflowStep(
                loanId,
                LoanStatus.REJECTED,
                actorUserId,
                actorEmail,
                remarks,
                null,
                null
        );
    }
}
