package com.personalloan.module.loan.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanApplicationRequest {
    private Long loanTypeId;
    private BigDecimal loanAmount;
    private Integer loanTenureMonths;
    private LoanPurpose purpose;
    private BigDecimal monthlyIncome;
    private BigDecimal existingEmis;
}
