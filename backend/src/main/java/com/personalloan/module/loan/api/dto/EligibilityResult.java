package com.personalloan.module.loan.api.dto;

import java.math.BigDecimal;
import java.util.List;

public record EligibilityResult(
        boolean eligible,
        List<String> reasons,
        BigDecimal maxEligibleAmount,
        BigDecimal foir
) {}
