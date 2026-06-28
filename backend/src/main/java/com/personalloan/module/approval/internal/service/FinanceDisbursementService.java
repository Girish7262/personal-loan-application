package com.personalloan.module.approval.internal.service;

import com.personalloan.module.loan.api.dto.LoanStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class FinanceDisbursementService {

    private final ApprovalWorkflowService workflowService;

    /**
     * Executes Finance Sanctioning.
     */
    public void sanctionApplication(
            Long loanId,
            Long actorUserId,
            String actorEmail,
            String remarks,
            BigDecimal approvedAmount,
            BigDecimal interestRate) {
        log.info("Finance Sanctioning triggered for Loan ID: {} by User ID: {}", loanId, actorUserId);
        workflowService.executeWorkflowStep(
                loanId,
                LoanStatus.SANCTIONED,
                actorUserId,
                actorEmail,
                remarks,
                approvedAmount,
                interestRate
        );
    }

    /**
     * Executes Finance Disbursement, releasing final funds.
     */
    public void disburseApplication(Long loanId, Long actorUserId, String actorEmail, String remarks) {
        log.info("Finance Disbursement triggered for Loan ID: {} by User ID: {}", loanId, actorUserId);
        workflowService.executeWorkflowStep(
                loanId,
                LoanStatus.DISBURSED,
                actorUserId,
                actorEmail,
                remarks,
                null,
                null
        );
    }
}
