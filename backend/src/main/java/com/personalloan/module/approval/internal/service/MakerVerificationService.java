package com.personalloan.module.approval.internal.service;

import com.personalloan.module.loan.api.dto.LoanStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MakerVerificationService {

    private final ApprovalWorkflowService workflowService;

    /**
     * Executes Maker Verification for a loan application.
     */
    public void verifyApplication(Long loanId, Long actorUserId, String actorEmail, String remarks) {
        log.info("Maker Verification triggered for Loan ID: {} by User ID: {}", loanId, actorUserId);
        workflowService.executeWorkflowStep(
                loanId,
                LoanStatus.VERIFIED,
                actorUserId,
                actorEmail,
                remarks,
                null,
                null
        );
    }
}
