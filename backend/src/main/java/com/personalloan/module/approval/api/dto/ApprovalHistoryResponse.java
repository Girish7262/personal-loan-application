package com.personalloan.module.approval.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalHistoryResponse {
    private Long approvalId;
    private Long loanId;
    private String action;
    private Long actorId;
    private String remarks;
    private BigDecimal recommendedAmount;
    private BigDecimal approvedAmount;
    private BigDecimal interestRate;
    private LocalDateTime actionDate;
}
