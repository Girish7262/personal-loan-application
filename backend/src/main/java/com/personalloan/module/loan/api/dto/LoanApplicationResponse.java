package com.personalloan.module.loan.api.dto;

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
public class LoanApplicationResponse {
    private Long loanId;
    private Long customerId;
    private LoanTypeResponse loanType;
    private String applicationNumber;
    private String loanStatus;
    private BigDecimal loanAmount;
    private BigDecimal approvedAmount;
    private Integer loanTenureMonths;
    private BigDecimal interestRate;
    private String purpose;
    private BigDecimal monthlyIncome;
    private BigDecimal existingEmis;
    private BigDecimal emi;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
    private Integer version;
}
