package com.personalloan.module.approval.internal.service;

import com.personalloan.module.approval.event.ApprovalCompletedEvent;
import com.personalloan.module.approval.internal.entity.ApprovalHistory;
import com.personalloan.module.approval.internal.repository.ApprovalHistoryRepository;
import com.personalloan.module.loan.api.LoanFacade;
import com.personalloan.module.loan.api.dto.LoanApplicationResponse;
import com.personalloan.module.loan.api.dto.LoanStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApprovalWorkflowService {

    private final LoanFacade loanFacade;
    private final ApprovalHistoryRepository approvalHistoryRepository;
    private final ApprovalValidationService validationService;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * Executes a status transition in the credit review workflow, logs the action to
     * approval history records, and publishes the ApprovalCompletedEvent.
     */
    @Transactional
    public void executeWorkflowStep(
            Long loanId,
            LoanStatus targetStatus,
            Long actorUserId,
            String actorEmail,
            String remarks,
            BigDecimal approvedAmount,
            BigDecimal interestRate) {

        log.info("Executing workflow step for Loan ID: {} towards status: {} by User ID: {}", 
                loanId, targetStatus, actorUserId);

        // 1. Fetch current loan details from the Loan module using the facade
        LoanApplicationResponse loan = loanFacade.getApplication(loanId, actorUserId);
        LoanStatus currentStatus = LoanStatus.valueOf(loan.getLoanStatus());

        // 2. Validate workflow transition pathways
        validationService.validateTransition(currentStatus, targetStatus);

        // 3. Reject actions require mandatory comments
        if (targetStatus == LoanStatus.REJECTED) {
            validationService.validateRemarksPresent(remarks, "REJECT");
        }

        // 4. Update status in the Loan module using the facade
        loanFacade.updateApplicationStatus(
                loanId,
                targetStatus,
                approvedAmount,
                interestRate,
                actorUserId,
                actorEmail
        );

        // 5. Write transition details to the local approval history logs table
        ApprovalHistory history = ApprovalHistory.builder()
                .loanId(loanId)
                .action(targetStatus.name())
                .actorId(actorUserId)
                .remarks(remarks)
                .recommendedAmount(approvedAmount)
                .approvedAmount(approvedAmount)
                .interestRate(interestRate)
                .build();
        approvalHistoryRepository.save(history);

        // 6. Publish ApprovalCompletedEvent
        eventPublisher.publishEvent(new ApprovalCompletedEvent(
                this,
                loanId,
                targetStatus.name(),
                actorUserId,
                remarks,
                approvedAmount,
                interestRate
        ));

        log.info("Workflow step successfully committed for Loan ID: {}", loanId);
    }
}
