package com.personalloan.module.customer.api.dto;

import java.math.BigDecimal;

public record CustomerSummary(
    Long customerId,
    Long userId,
    String firstName,
    String lastName,
    String email,
    String panNumber,
    BigDecimal monthlyIncome,
    String employmentType,
    ProfileStatus profileStatus,
    int profileCompletionPercentage,
    boolean kycVerified
) {}
