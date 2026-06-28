package com.personalloan.module.approval.api;

import com.personalloan.module.approval.api.dto.ApprovalHistoryResponse;

import java.math.BigDecimal;
import java.util.List;

public interface ApprovalFacade {

    /**
     * Retrieves the list of workflow log audits for a loan application.
     */
    List<ApprovalHistoryResponse> getApprovalHistory(Long loanId);

    /**
     * Persists a new workflow logs action.
     */
    void logApprovalHistory(
            Long loanId,
            String action,
            Long actorId,
            String remarks,
            BigDecimal recommendedAmount,
            BigDecimal approvedAmount,
            BigDecimal interestRate
    );
}
